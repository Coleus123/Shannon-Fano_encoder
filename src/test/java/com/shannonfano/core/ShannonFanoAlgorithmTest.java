package com.shannonfano.core;

import com.shannonfano.exception.InvalidArchiveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

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
     * Проверяет полный цикл для нескольких файлов
     */
    @Test
    void testEncodeDecodeMultipleFiles() throws Exception {
        Path file1 = tempDir.resolve("file1.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file1.toFile()), StandardCharsets.UTF_8))) {
            writer.write("content1");
        }
        Path file2 = tempDir.resolve("file2.bin");
        try (FileOutputStream fos = new FileOutputStream(file2.toFile())) {
            fos.write(new byte[]{(byte) 0xAA, (byte) 0xBB});
        }
        Path archive = tempDir.resolve("archive.sf");
        Path outputDir = tempDir.resolve("output");
        algorithm.encode(Arrays.asList(file1.toString(), file2.toString()), archive.toString());
        algorithm.decode(archive.toString(), outputDir.toString());
        assertTrue(outputDir.resolve("file1.txt").toFile().exists());
        assertTrue(outputDir.resolve("file2.bin").toFile().exists());
        assertEquals("content1", readTextFile(outputDir.resolve("file1.txt").toString()));
        assertArrayEquals(new byte[]{(byte) 0xAA, (byte) 0xBB}, readBinaryFile(outputDir.resolve("file2.bin").toString()));
    }

    /**
     * Проверяет полный цикл для директории с вложенными файлами
     */
    @Test
    void testEncodeDecodeDirectory() throws Exception {
        Path dir = tempDir.resolve("testdir");
        dir.toFile().mkdirs();
        Path file1 = dir.resolve("file1.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file1.toFile()), StandardCharsets.UTF_8))) {
            writer.write("text1");
        }
        Path subdir = dir.resolve("subdir");
        subdir.toFile().mkdirs();
        Path file2 = subdir.resolve("file2.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file2.toFile()), StandardCharsets.UTF_8))) {
            writer.write("text2");
        }
        Path file3 = subdir.resolve("file3.bin");
        try (FileOutputStream fos = new FileOutputStream(file3.toFile())) {
            fos.write(new byte[]{(byte) 0xCC, (byte) 0xDD});
        }
        Path archive = tempDir.resolve("archive.sf");
        Path outputDir = tempDir.resolve("output");
        algorithm.encode(List.of(dir.toString()), archive.toString());
        algorithm.decode(archive.toString(), outputDir.toString());
        assertTrue(outputDir.resolve("testdir").resolve("file1.txt").toFile().exists());
        assertTrue(outputDir.resolve("testdir").resolve("subdir").resolve("file2.txt").toFile().exists());
        assertTrue(outputDir.resolve("testdir").resolve("subdir").resolve("file3.bin").toFile().exists());
        assertEquals("text1", readTextFile(outputDir.resolve("testdir").resolve("file1.txt").toString()));
        assertEquals("text2", readTextFile(outputDir.resolve("testdir").resolve("subdir").resolve("file2.txt").toString()));
        assertArrayEquals(new byte[]{(byte) 0xCC, (byte) 0xDD}, readBinaryFile(outputDir.resolve("testdir").resolve("subdir").resolve("file3.bin").toString()));
    }

    /**
     * Проверяет полный цикл для смешанных путей (файлы + директории)
     */
    @Test
    void testEncodeDecodeMixedPaths() throws Exception {
        Path file1 = tempDir.resolve("root.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file1.toFile()), StandardCharsets.UTF_8))) {
            writer.write("root");
        }
        Path dir = tempDir.resolve("mydir");
        dir.toFile().mkdirs();
        Path file2 = dir.resolve("inner.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file2.toFile()), StandardCharsets.UTF_8))) {
            writer.write("inner");
        }
        Path archive = tempDir.resolve("archive.sf");
        Path outputDir = tempDir.resolve("output");
        algorithm.encode(Arrays.asList(file1.toString(), dir.toString()), archive.toString());
        algorithm.decode(archive.toString(), outputDir.toString());
        assertTrue(outputDir.resolve("root.txt").toFile().exists());
        assertTrue(outputDir.resolve("mydir").resolve("inner.txt").toFile().exists());
        assertEquals("root", readTextFile(outputDir.resolve("root.txt").toString()));
        assertEquals("inner", readTextFile(outputDir.resolve("mydir").resolve("inner.txt").toString()));
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
     * Проверяет, что при попытке декодирования несуществующего архива выбрасывается исключение
     */
    @Test
    void testDecodeWithMissingArchive() {
        assertThrows(FileNotFoundException.class,
                () -> algorithm.decode("nonexistent.sf", tempDir.toString()));
    }

    /**
     * Проверяет, что при попытке декодирования файла с неверной сигнатурой выбрасывается исключение
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