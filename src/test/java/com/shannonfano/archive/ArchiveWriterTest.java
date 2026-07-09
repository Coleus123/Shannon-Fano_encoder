package com.shannonfano.archive;

import com.shannonfano.core.ShannonFanoAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для записи в архива
 */
class ArchiveWriterTest {

    @TempDir
    Path tempDir;

    /**
     * Проверяет, что текстовый файл корректно записывается в архив
     *
     */
    @Test
    void testWriteTextFile() throws Exception {
        Path textFile = tempDir.resolve("test.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(textFile.toFile()), StandardCharsets.UTF_8))) {
            writer.write("hello world");
        }
        Path archivePath = tempDir.resolve("archive.sf");
        ShannonFanoAlgorithm algorithm = new ShannonFanoAlgorithm();
        algorithm.encode(textFile.toString(), archivePath.toString());
        assertTrue(archivePath.toFile().exists());
        assertTrue(archivePath.toFile().length() > 0);
    }

    /**
     * Проверяет, что бинарный файл корректно записывается в архив
     *
     */
    @Test
    void testWriteBinaryFile() throws Exception {
        Path binFile = tempDir.resolve("test.bin");
        try (FileOutputStream fos = new FileOutputStream(binFile.toFile())) {
            fos.write(new byte[]{0x00, 0x01, 0x02, 0x03});
        }
        Path archivePath = tempDir.resolve("archive.sf");
        ShannonFanoAlgorithm algorithm = new ShannonFanoAlgorithm();
        algorithm.encode(binFile.toString(), archivePath.toString());
        assertTrue(archivePath.toFile().exists());
        assertTrue(archivePath.toFile().length() > 0);
    }

    /**
     * Проверяет, что пустой файл корректно записывается в архив
     *
     */
    @Test
    void testWriteEmptyFile() throws Exception {
        Path emptyFile = tempDir.resolve("empty.txt");
        emptyFile.toFile().createNewFile();
        Path archivePath = tempDir.resolve("archive.sf");
        ShannonFanoAlgorithm algorithm = new ShannonFanoAlgorithm();
        algorithm.encode(emptyFile.toString(), archivePath.toString());
        assertTrue(archivePath.toFile().exists());
    }
}