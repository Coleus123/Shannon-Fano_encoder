package com.shannonfano.core;

import com.shannonfano.exception.CorruptArchiveException;
import com.shannonfano.exception.InvalidArchiveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для алгоритма Шеннона-Фано
 */
class ShannonFanoAlgorithmTest {

    private final ShannonFanoAlgorithm algorithm = new ShannonFanoAlgorithm();

    @TempDir
    Path tempDir;

    /**
     * Проверяет полный цикл для текстового файла
     */
    @Test
    void testEncodeDecodeTextFile() throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(inputFile.toFile()), StandardCharsets.UTF_8))) {
            writer.write("text");
        }
        Path archive = tempDir.resolve("archive.sf");
        Path outputDir = tempDir.resolve("output");
        algorithm.encode(inputFile.toString(), archive.toString());
        algorithm.decode(archive.toString(), outputDir.toString());
        Path decodedFile = outputDir.resolve("input.txt");
        assertTrue(decodedFile.toFile().exists());
        String content = readTextFile(decodedFile.toString());
        assertEquals("text", content);
    }

    /**
     * Проверяет полный цикл для бинарного файла
     */
    @Test
    void testEncodeDecodeBinaryFile() throws Exception {
        Path inputFile = tempDir.resolve("input.bin");
        try (FileOutputStream fos = new FileOutputStream(inputFile.toFile())) {
            fos.write(new byte[]{0x00, 0x01, 0x02, 0x03});
        }
        Path archive = tempDir.resolve("archive.sf");
        Path outputDir = tempDir.resolve("output");
        algorithm.encode(inputFile.toString(), archive.toString());
        algorithm.decode(archive.toString(), outputDir.toString());
        Path decodedFile = outputDir.resolve("input.bin");
        assertTrue(decodedFile.toFile().exists());
        byte[] content = readBinaryFile(decodedFile.toString());
        assertArrayEquals(new byte[]{0x00, 0x01, 0x02, 0x03}, content);
    }

    /**
     * Проверяет полный цикл для пустого файла
     */
    @Test
    void testEncodeDecodeEmptyFile() throws Exception {
        Path inputFile = tempDir.resolve("empty.txt");
        inputFile.toFile().createNewFile();

        Path archive = tempDir.resolve("archive.sf");
        Path outputDir = tempDir.resolve("output");

        algorithm.encode(inputFile.toString(), archive.toString());
        algorithm.decode(archive.toString(), outputDir.toString());

        Path decodedFile = outputDir.resolve("empty.txt");
        assertTrue(decodedFile.toFile().exists());
        assertEquals(0, decodedFile.toFile().length());
    }

    /**
     * Проверяет, что при попытке кодирования несуществующего файла выбрасывается исключение
     */
    @Test
    void testEncodeWithMissingFile() {
        assertThrows(FileNotFoundException.class,
                () -> algorithm.encode("nonexistent.txt", tempDir.resolve("archive.sf").toString()));
    }

    /**
     * Проверяет, что при попытке кодирования директории выбрасывается исключение
     */
    @Test
    void testEncodeWithDirectory() {
        assertThrows(IllegalArgumentException.class,
                () -> algorithm.encode(tempDir.toString(), tempDir.resolve("archive.sf").toString()));
    }

    /**
     * Проверяет, что при попытке декодирования
     * несуществующего архива выбрасывается исключение
     */
    @Test
    void testDecodeWithMissingArchive() {
        assertThrows(FileNotFoundException.class,
                () -> algorithm.decode("nonexistent.sf", tempDir.toString()));
    }

    /**
     * Проверяет, что при попытке декодирования
     * файла с неверной сигнатурой выбрасывается исключение
     */
    @Test
    void testInvalidArchiveFormat() throws IOException {
        Path invalidFile = tempDir.resolve("invalid.sf");
        try (DataOutputStream out = new DataOutputStream(
                new FileOutputStream(invalidFile.toFile()))) {
            out.writeBytes("INVALID");
        }
        assertThrows(InvalidArchiveException.class,
                () -> algorithm.decode(invalidFile.toString(), tempDir.toString()));
    }
    /**
     * Считывает текстовый файл в строку
     *
     * @param path путь к файлу
     * @return содержимое файла
     */
    private String readTextFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append('\n');
            }
            if (content.length() > 0) {
                content.deleteCharAt(content.length() - 1);
            }
        }
        return content.toString();
    }
    /**
     * Читает бинарный файл в массив байтов
     *
     * @param path путь к файлу
     * @return содержимое файла в виде байтового массива
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