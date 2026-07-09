package com.shannonfano.archive;

import com.shannonfano.codec.BitStringEncoder;
import com.shannonfano.file.FileTypeDetector;
import com.shannonfano.file.TextFileHandler;
import com.shannonfano.file.BinaryFileHandler;
import com.shannonfano.hash.HashService;

import java.io.*;
import java.util.Map;

/**
 * Записывает файл в архив в формате Shannon-Fano
 */
public class ArchiveWriter {

    private final HashService hashService = new HashService();
    private final BitStringEncoder bitStringEncoder = new BitStringEncoder();
    private final FileTypeDetector typeDetector = new FileTypeDetector();

    /**
     * Записывает файл в архив
     *
     * @param filePath Путь к исходному файлу
     * @param outputPath Путь к выходному архиву
     * @param encodingTable Таблица кодирования Шеннона-Фано
     */
    public void writeFile(String filePath, String outputPath,
                          Map<Object, String> encodingTable) throws IOException {
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputPath)))) {
            out.write(ArchiveMetadata.SIGNATURE);
            out.writeInt(1);
            writeSingleFile(filePath, out, encodingTable);
        }
    }

    /**
     * Записывает один файл в архив
     *
     * @param filePath Путь к файлу
     * @param out Поток для записи
     * @param encodingTable Таблица кодирования
     */
    private void writeSingleFile(String filePath, DataOutputStream out,
                                 Map<Object, String> encodingTable) throws IOException {
        boolean isText = typeDetector.isTextFile(filePath);

        if (isText) {
            TextFileHandler handler = new TextFileHandler();
            String content = handler.read(filePath);
            out.write(ArchiveMetadata.FILE);
            out.writeUTF(new File(filePath).getName());
            out.write(ArchiveMetadata.TEXT);
            out.writeUTF(hashService.calculateHash(content));
            writeEncodingTable(out, encodingTable);
            writeEncodedContent(content, out, encodingTable);
        } else {
            BinaryFileHandler handler = new BinaryFileHandler();
            byte[] content = handler.read(filePath);
            out.write(ArchiveMetadata.FILE);
            out.writeUTF(new File(filePath).getName());
            out.write(ArchiveMetadata.BINARY);
            out.writeUTF(hashService.calculateHash(content));
            writeEncodingTable(out, encodingTable);
            writeEncodedContent(content, out, encodingTable);
        }
    }

    /**
     * Записывает таблицу кодирования в поток
     *
     * @param out Поток для записи
     * @param encodingTable Таблица кодирования
     */
    private void writeEncodingTable(DataOutputStream out, Map<Object, String> encodingTable) throws IOException {
        out.writeInt(encodingTable.size());
        for (Map.Entry<Object, String> entry : encodingTable.entrySet()) {
            if (entry.getKey() instanceof Character) {
                out.writeBoolean(true);
                out.writeChar((Character) entry.getKey());
            } else {
                out.writeBoolean(false);
                out.writeByte((Byte) entry.getKey());
            }
            out.writeUTF(entry.getValue());
        }
    }

    /**
     * Кодирует и записывает текстовое содержимое
     *
     * @param content Строка для кодирования
     * @param out Поток для записи
     * @param encodingTable Таблица кодирования
     */
    private void writeEncodedContent(String content, DataOutputStream out,
                                     Map<Object, String> encodingTable) throws IOException {
        String bitString = bitStringEncoder.encode(content, encodingTable);
        writeBitString(bitString, out);
    }

    /**
     * Кодирует и записывает бинарное содержимое
     *
     * @param content Байтовый массив для кодирования
     * @param out Поток для записи
     * @param encodingTable Таблица кодирования
     */
    private void writeEncodedContent(byte[] content, DataOutputStream out,
                                     Map<Object, String> encodingTable) throws IOException {
        String bitString = bitStringEncoder.encode(content, encodingTable);
        writeBitString(bitString, out);
    }

    /**
     * Записывает битовую строку в поток
     *
     * @param bitString Битовая строка
     * @param out Поток для записи
     */
    private void writeBitString(String bitString, DataOutputStream out) throws IOException {
        int bitCount = bitString.length();
        out.writeInt(bitCount);
        out.writeUTF(hashService.calculateHash(bitString));

        if (bitCount == 0) {
            return;
        }

        int padding = (8 - (bitCount % 8)) % 8;
        String paddedBitString = bitString + "0".repeat(padding);
        for (int i = 0; i < paddedBitString.length(); i += 8) {
            String byteStr = paddedBitString.substring(i, Math.min(i + 8, paddedBitString.length()));
            int byteVal = Integer.parseInt(byteStr, 2);
            out.writeByte(byteVal);
        }
    }
}