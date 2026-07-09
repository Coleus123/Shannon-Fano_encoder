package com.shannonfano.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для обработчика бинарных файлов
 */
class BinaryFileHandlerTest {

    private final BinaryFileHandler handler = new BinaryFileHandler();

    @TempDir
    Path tempDir;

    /**
     * Проверяет, что бинарный файл корректно записывается и читается
     */
    @Test
    void testReadWriteBinaryFile() throws IOException {
        byte[] content = {0x00, 0x01, 0x02, 0x03, (byte) 0xFF, 0x7F, (byte) 0x80};
        Path file = tempDir.resolve("test.bin");
        handler.write(file.toString(), content);
        byte[] readContent = handler.read(file.toString());
        assertArrayEquals(content, readContent);
    }

    /**
     * Проверяет, что пустой файл корректно записывается и читается
     */
    @Test
    void testEmptyFile() throws IOException {
        byte[] content = new byte[0];
        Path file = tempDir.resolve("empty.bin");
        handler.write(file.toString(), content);
        byte[] readContent = handler.read(file.toString());
        assertArrayEquals(content, readContent);
    }

    /**
     * Проверяет, что большой файл корректно записывается и читается
     */
    @Test
    void testLargeFile() throws IOException {
        byte[] content = new byte[10000];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        Path file = tempDir.resolve("large.bin");
        handler.write(file.toString(), content);
        byte[] readContent = handler.read(file.toString());
        assertArrayEquals(content, readContent);
    }
}