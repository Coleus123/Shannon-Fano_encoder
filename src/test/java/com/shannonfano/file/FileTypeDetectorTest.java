package com.shannonfano.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для детектора типа файла
 */
class FileTypeDetectorTest {

    private final FileTypeDetector detector = new FileTypeDetector();

    @TempDir
    Path tempDir;

    /**
     * Проверяет, что текстовый файл определяется как текстовый,
     * а бинарный файл - как бинарный.
     */
    @Test
    void testIsTextFile() throws IOException {
        Path textFile = tempDir.resolve("test.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(textFile.toFile()), StandardCharsets.UTF_8))) {
            writer.write("Это текстовый файл");
        }
        assertTrue(detector.isTextFile(textFile.toString()));
        Path binFile = tempDir.resolve("test.bin");
        try (FileOutputStream fos = new FileOutputStream(binFile.toFile())) {
            fos.write(new byte[]{0x00, 0x01, 0x02, 0x03, (byte) 0xFF});
        }
        assertFalse(detector.isTextFile(binFile.toString()));
    }
}