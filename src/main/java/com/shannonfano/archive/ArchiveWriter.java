package com.shannonfano.archive;

import com.shannonfano.codec.BitStringEncoder;
import com.shannonfano.file.FileTypeDetector;
import com.shannonfano.file.TextFileHandler;
import com.shannonfano.file.BinaryFileHandler;
import com.shannonfano.hash.HashService;

import java.io.*;
import java.util.*;

/**
 * Записывает файл в архив в формате Shannon-Fano
 */
public class ArchiveWriter {

    private final HashService hashService = new HashService();
    private final BitStringEncoder bitStringEncoder = new BitStringEncoder();
    private final FileTypeDetector typeDetector = new FileTypeDetector();

    /**
     * Записывает файл в архив с паролем
     *
     * @param filePath Путь к исходному файлу
     * @param outputPath Путь к выходному архиву
     * @param encodingTable Таблица кодирования Шеннона-Фано
     * @param password Пароль для защиты архива
     */
    public void writeFile(String filePath, String outputPath,
                          Map<Object, String> encodingTable, String password) throws IOException {
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputPath)))) {
            out.write(ArchiveMetadata.SIGNATURE);
            writePassword(out, password);
            out.writeInt(1);
            writeSingleFile(filePath, out, encodingTable);
        }
    }

    /**
     * Записывает коллекцию файлов и директорий в архив с паролем
     *
     * @param inputPaths Список путей
     * @param outputPath Путь к выходному архиву
     * @param encodingTable Таблица кодирования
     * @param password Пароль для защиты архива
     */
    public void writeArchive(List<String> inputPaths, String outputPath,
                             Map<Object, String> encodingTable, String password) throws IOException {
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputPath)))) {
            out.write(ArchiveMetadata.SIGNATURE);
            writePassword(out, password);
            out.writeInt(inputPaths.size());
            for (String path : inputPaths) {
                File file = new File(path);
                if (file.isFile()) {
                    writeSingleFile(path, out, encodingTable);
                } else if (file.isDirectory()) {
                    writeDirectory(path, out, encodingTable);
                }
            }
        }
    }

    /**
     * Записывает маркер пароля в архив
     *
     * @param out Поток для записи
     * @param password Пароль
     */
    private void writePassword(DataOutputStream out, String password) throws IOException {
        if (password != null && !password.isEmpty()) {
            out.write(ArchiveMetadata.PASSWORD);
            out.writeUTF(hashService.calculateHash(password));
        } else {
            out.write(ArchiveMetadata.NO_PASSWORD);
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
        out.write(ArchiveMetadata.FILE);
        out.writeUTF(new File(filePath).getName());
        if (isText) {
            TextFileHandler handler = new TextFileHandler();
            String content = handler.read(filePath);
            out.write(ArchiveMetadata.TEXT);
            out.writeUTF(hashService.calculateHash(content));
            writeEncodingTable(out, encodingTable);
            writeEncodedContent(content, out, encodingTable);
        } else {
            BinaryFileHandler handler = new BinaryFileHandler();
            byte[] content = handler.read(filePath);
            out.write(ArchiveMetadata.BINARY);
            out.writeUTF(hashService.calculateHash(content));
            writeEncodingTable(out, encodingTable);
            writeEncodedContent(content, out, encodingTable);
        }
    }

    /**
     * Записывает директорию в архив
     *
     * @param dirPath Путь к директории
     * @param out Поток для записи
     * @param encodingTable Таблица кодирования
     */
    private void writeDirectory(String dirPath, DataOutputStream out,
                                Map<Object, String> encodingTable) throws IOException {
        out.write(ArchiveMetadata.CATALOG);
        out.writeUTF(new File(dirPath).getName());
        writeEncodingTable(out, encodingTable);
        List<String> subdirs = new ArrayList<>();
        List<FileEntry> binaryFiles = new ArrayList<>();
        List<FileEntry> textFiles = new ArrayList<>();
        collectDirectoryItems(dirPath, dirPath, subdirs, binaryFiles, textFiles);
        out.writeInt(subdirs.size());
        for (String relPath : subdirs) {
            out.writeUTF(relPath);
        }
        out.writeInt(binaryFiles.size());
        for (FileEntry entry : binaryFiles) {
            out.writeUTF(entry.relativePath);
            byte[] content = new BinaryFileHandler().read(entry.absolutePath);
            out.writeUTF(hashService.calculateHash(content));
            writeEncodedContent(content, out, encodingTable);
        }
        out.writeInt(textFiles.size());
        for (FileEntry entry : textFiles) {
            out.writeUTF(entry.relativePath);
            String content = new TextFileHandler().read(entry.absolutePath);
            out.writeUTF(hashService.calculateHash(content));
            writeEncodedContent(content, out, encodingTable);
        }
    }

    /**
     * Рекурсивно собирает элементы директории
     *
     * @param baseDir Базовая директория
     * @param currentDir Текущая директория
     * @param subdirs Список поддиректорий
     * @param binaryFiles Список бинарных файлов
     * @param textFiles Список текстовых файлов
     */
    private void collectDirectoryItems(String baseDir, String currentDir,
                                       List<String> subdirs,
                                       List<FileEntry> binaryFiles,
                                       List<FileEntry> textFiles) {
        File dir = new File(currentDir);
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            String relPath = getRelativePath(baseDir, file.getAbsolutePath());
            if (file.isDirectory()) {
                subdirs.add(relPath);
                collectDirectoryItems(baseDir, file.getAbsolutePath(), subdirs, binaryFiles, textFiles);
            } else if (file.isFile()) {
                if (typeDetector.isTextFile(file.getAbsolutePath())) {
                    textFiles.add(new FileEntry(relPath, file.getAbsolutePath()));
                } else {
                    binaryFiles.add(new FileEntry(relPath, file.getAbsolutePath()));
                }
            }
        }
    }

    /**
     * Вычисляет относительный путь
     *
     * @param baseDir Базовая директория
     * @param absolutePath Абсолютный путь
     * @return Относительный путь
     */
    private String getRelativePath(String baseDir, String absolutePath) {
        String base = baseDir.replace('\\', '/');
        String abs = absolutePath.replace('\\', '/');
        if (abs.startsWith(base)) {
            return abs.substring(base.length() + 1);
        }
        return abs;
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

    /**
     * Вспомогательный класс для хранения информации о файле
     */
    private static class FileEntry {
        final String relativePath;
        final String absolutePath;
        FileEntry(String relativePath, String absolutePath) {
            this.relativePath = relativePath;
            this.absolutePath = absolutePath;
        }
    }
}