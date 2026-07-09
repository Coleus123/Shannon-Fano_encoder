package com.shannonfano.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для обработчика текстовых файлов
 */
class TextFileHandlerTest {

    private final TextFileHandler handler = new TextFileHandler();

    @TempDir
    Path tempDir;

    /**
     * Проверяет, что текстовый файл корректно записывается и читается
     */
    @Test
    void testReadWriteTextFile() throws IOException {
        String content = "Hello\nWorld\nTest with русский текст";
        Path file = tempDir.resolve("test.txt");

        handler.write(file.toString(), content);
        String readContent = handler.read(file.toString());
        assertEquals(content, readContent);
    }

    /**
     * Проверяет, что пустой файл корректно записывается и читается
     */
    @Test
    void testEmptyFile() throws IOException {
        String content = "";
        Path file = tempDir.resolve("empty.txt");

        handler.write(file.toString(), content);
        String readContent = handler.read(file.toString());
        assertEquals(content, readContent);
    }

    /**
     * Проверяет, что файл с переносами строк корректно записывается и читается
     */
    @Test
    void testFileWithNewlines() throws IOException {
        String content = "Line1\nLine2\nLine3";
        Path file = tempDir.resolve("test.txt");

        handler.write(file.toString(), content);
        String readContent = handler.read(file.toString());
        assertEquals(content, readContent);
    }
}