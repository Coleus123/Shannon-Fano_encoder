package com.shannonfano.archive;

import com.shannonfano.core.ShannonFanoAlgorithm;
import com.shannonfano.exception.CorruptArchiveException;
import com.shannonfano.exception.InvalidArchiveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для считывателя архива
 */
class ArchiveReaderTest {

    @TempDir
    Path tempDir;

    /**
     * Проверяет, что текстовый файл корректно восстанавливается из архива
     *
     */
    @Test
    void testReadTextFile() throws Exception {
        String content = "hello world";
        Path inputFile = tempDir.resolve("input.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(inputFile.toFile()), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
        ShannonFanoAlgorithm algorithm = new ShannonFanoAlgorithm();
        Path archivePath = tempDir.resolve("archive.sf");
        algorithm.encode(inputFile.toString(), archivePath.toString());
        Path outputDir = tempDir.resolve("output");
        ArchiveReader reader = new ArchiveReader();
        reader.readArchive(archivePath.toString(), outputDir.toString());
        Path outputFile = outputDir.resolve("input.txt");
        assertTrue(outputFile.toFile().exists());
        assertEquals(content, readTextFile(outputFile.toString()));
    }

    /**
     * Проверяет, что бинарный файл корректно восстанавливается из архива
     *
     */
    @Test
    void testReadBinaryFile() throws Exception {
        byte[] content = {0x00, 0x01, 0x02, 0x03};
        Path inputFile = tempDir.resolve("input.bin");
        try (FileOutputStream fos = new FileOutputStream(inputFile.toFile())) {
            fos.write(content);
        }
        ShannonFanoAlgorithm algorithm = new ShannonFanoAlgorithm();
        Path archivePath = tempDir.resolve("archive.sf");
        algorithm.encode(inputFile.toString(), archivePath.toString());
        Path outputDir = tempDir.resolve("output");
        ArchiveReader reader = new ArchiveReader();
        reader.readArchive(archivePath.toString(), outputDir.toString());
        Path outputFile = outputDir.resolve("input.bin");
        assertTrue(outputFile.toFile().exists());
        assertArrayEquals(content, readBinaryFile(outputFile.toString()));
    }

    /**
     * Проверяет, что при неверной сигнатуре архива выбрасывается InvalidArchiveException
     *
     */
    @Test
    void testInvalidSignature() throws IOException {
        Path invalidFile = tempDir.resolve("invalid.sf");
        try (DataOutputStream out = new DataOutputStream(
                new FileOutputStream(invalidFile.toFile()))) {
            out.writeBytes("INVALID");
        }
        ArchiveReader reader = new ArchiveReader();
        assertThrows(InvalidArchiveException.class,
                () -> reader.readArchive(invalidFile.toString(), tempDir.toString()));
    }

    /**
     * Читает текстовый файл и возвращает его содержимое
     *
     * @param path Путь к файлу
     * @return Содержимое файла
     */
    private String readTextFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    /**
     * Читает бинарный файл и возвращает его содержимое
     *
     * @param path Путь к файлу
     * @return Содержимое файла
     */
    private byte[] readBinaryFile(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }
}