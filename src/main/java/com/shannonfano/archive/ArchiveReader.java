package com.shannonfano.archive;

import com.shannonfano.codec.BitStringDecoder;
import com.shannonfano.exception.CorruptArchiveException;
import com.shannonfano.exception.InvalidArchiveException;
import com.shannonfano.file.FileHandler;
import com.shannonfano.file.TextFileHandler;
import com.shannonfano.file.BinaryFileHandler;
import com.shannonfano.hash.HashService;

import java.io.*;
import java.util.*;

/**
 * Читает архив и восстанавливает файл,
 * проверяет целостность данных через хеширование
 */
public class ArchiveReader {

    private final HashService hashService = new HashService();
    private final BitStringDecoder bitStringDecoder = new BitStringDecoder();

    /**
     * Читает архив и восстанавливает файл в указанную директорию
     *
     * @param inputPath Путь к архиву
     * @param outputDir Директория для восстановления файла
     */
    public void readArchive(String inputPath, String outputDir) throws IOException,
            InvalidArchiveException,
            CorruptArchiveException {
        readArchive(inputPath, outputDir, null);
    }

    /**
     * Читает архив и восстанавливает файл в указанную директорию с паролем
     *
     * @param inputPath Путь к архиву
     * @param outputDir Директория для восстановления файла
     * @param password Пароль для архива
     */
    public void readArchive(String inputPath, String outputDir, String password) throws IOException,
            InvalidArchiveException,
            CorruptArchiveException {
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(inputPath)))) {
            byte[] signature = new byte[4];
            in.readFully(signature);
            if (!Arrays.equals(signature, ArchiveMetadata.SIGNATURE)) {
                throw new InvalidArchiveException("Неверный формат файла или версия архива");
            }
            byte[] passwordMarker = new byte[4];
            in.readFully(passwordMarker);
            if (Arrays.equals(passwordMarker, ArchiveMetadata.PASSWORD)) {
                String storedHash = in.readUTF();
                if (password == null || password.isEmpty()) {
                    throw new InvalidArchiveException("Архив защищен паролем");
                }
                if (!hashService.calculateHash(password).equals(storedHash)) {
                    throw new InvalidArchiveException("Неверный пароль");
                }
            } else if (!Arrays.equals(passwordMarker, ArchiveMetadata.NO_PASSWORD)) {
                throw new InvalidArchiveException("Поврежденный архив");
            }
            int numItems = in.readInt();
            for (int i = 0; i < numItems; i++) {
                byte[] itemType = new byte[4];
                in.readFully(itemType);
                if (Arrays.equals(itemType, ArchiveMetadata.FILE)) {
                    readFile(in, outputDir);
                } else if (Arrays.equals(itemType, ArchiveMetadata.CATALOG)) {
                    readDirectory(in, outputDir);
                } else {
                    throw new InvalidArchiveException("Неизвестный тип элемента: " + new String(itemType));
                }
            }
        }
    }

    /**
     * Читает один файл из архива и восстанавливает его.
     * Проверяет хеш битовой строки и хеш содержимого.
     *
     * @param in Поток для чтения
     * @param outputDir Директория для восстановления
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void readFile(DataInputStream in, String outputDir) throws IOException, CorruptArchiveException {
        String filename = in.readUTF();
        byte[] fileType = new byte[4];
        in.readFully(fileType);
        String originalHash = in.readUTF();
        Map<Object, String> encodingTable = readEncodingTable(in);
        Map<String, Object> decodingTable = new HashMap<>();
        for (Map.Entry<Object, String> entry : encodingTable.entrySet()) {
            decodingTable.put(entry.getValue(), entry.getKey());
        }
        int bitCount = in.readInt();
        String codedHash = in.readUTF();
        int byteCount = (bitCount + 7) / 8;
        byte[] encodedBytes = new byte[byteCount];
        in.readFully(encodedBytes);
        String bitString = bitStringDecoder.bytesToBitString(encodedBytes, bitCount);
        if (!codedHash.equals(hashService.calculateHash(bitString))) {
            System.out.println("Предупреждение: битовая строка повреждена, файл возможно поврежден");
        }
        Object content;
        if (Arrays.equals(fileType, ArchiveMetadata.TEXT)) {
            content = bitStringDecoder.decodeText(bitString, decodingTable);
        } else {
            content = bitStringDecoder.decodeBinary(bitString, decodingTable);
        }
        String decodedHash;
        if (content instanceof String) {
            decodedHash = hashService.calculateHash((String) content);
        } else {
            decodedHash = hashService.calculateHash((byte[]) content);
        }
        if (!decodedHash.equals(originalHash)) {
            System.out.printf("Предупреждение: файл %s восстановлен, но не соответствует оригиналу%n",
                    outputDir + File.separator + filename);
        }
        FileHandler handler = Arrays.equals(fileType, ArchiveMetadata.TEXT) ?
                new TextFileHandler() : new BinaryFileHandler();
        handler.write(outputDir + File.separator + filename, content);
    }

    /**
     * Читает директорию из архива и восстанавливает её
     *
     * @param in Поток для чтения
     * @param outputDir Директория для восстановления
     */
    private void readDirectory(DataInputStream in, String outputDir) throws IOException, CorruptArchiveException {
        String dirname = in.readUTF();
        Map<Object, String> encodingTable = readEncodingTable(in);
        Map<String, Object> decodingTable = new HashMap<>();
        for (Map.Entry<Object, String> entry : encodingTable.entrySet()) {
            decodingTable.put(entry.getValue(), entry.getKey());
        }
        File mainDir = new File(outputDir, dirname);
        mainDir.mkdirs();
        int numSubdirs = in.readInt();
        for (int i = 0; i < numSubdirs; i++) {
            String relPath = in.readUTF();
            new File(mainDir, relPath).mkdirs();
        }
        int numBinFiles = in.readInt();
        for (int i = 0; i < numBinFiles; i++) {
            readFileContent(in, mainDir, decodingTable, false);
        }
        int numTextFiles = in.readInt();
        for (int i = 0; i < numTextFiles; i++) {
            readFileContent(in, mainDir, decodingTable, true);
        }
    }

    /**
     * Читает содержимое файла из архива
     *
     * @param in Поток для чтения
     * @param baseDir Базовая директория
     * @param decodingTable Таблица декодирования
     * @param isText true для текстового файла
     */
    private void readFileContent(DataInputStream in, File baseDir,
                                 Map<String, Object> decodingTable, boolean isText) throws IOException, CorruptArchiveException {
        String relPath = in.readUTF();
        String originalHash = in.readUTF();
        int bitCount = in.readInt();
        String codedHash = in.readUTF();
        int byteCount = (bitCount + 7) / 8;
        byte[] encodedBytes = new byte[byteCount];
        in.readFully(encodedBytes);
        String bitString = bitStringDecoder.bytesToBitString(encodedBytes, bitCount);
        if (!codedHash.equals(hashService.calculateHash(bitString))) {
            System.out.println("Предупреждение: битовая строка повреждена, файл возможно поврежден");
        }
        Object content;
        if (isText) {
            content = bitStringDecoder.decodeText(bitString, decodingTable);
        } else {
            content = bitStringDecoder.decodeBinary(bitString, decodingTable);
        }
        String decodedHash;
        if (content instanceof String) {
            decodedHash = hashService.calculateHash((String) content);
        } else {
            decodedHash = hashService.calculateHash((byte[]) content);
        }
        File outputFile = new File(baseDir, relPath);
        if (!decodedHash.equals(originalHash)) {
            System.out.printf("Предупреждение: файл %s восстановлен, но не соответствует оригиналу%n",
                    outputFile.getAbsolutePath());
        }
        FileHandler handler = isText ? new TextFileHandler() : new BinaryFileHandler();
        handler.write(outputFile.getAbsolutePath(), content);
    }

    /**
     * Читает таблицу кодирования из потока
     *
     * @param in Поток для чтения
     * @return Таблица кодирования
     */
    private Map<Object, String> readEncodingTable(DataInputStream in) throws IOException {
        Map<Object, String> table = new HashMap<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            boolean isChar = in.readBoolean();
            Object key;
            if (isChar) {
                key = in.readChar();
            } else {
                key = in.readByte();
            }
            String value = in.readUTF();
            table.put(key, value);
        }
        return table;
    }
}