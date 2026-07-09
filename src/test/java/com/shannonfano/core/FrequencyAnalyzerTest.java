package com.shannonfano.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для анализатора частот.
 */
class FrequencyAnalyzerTest {

    @TempDir
    Path tempDir;

    /**
     * Проверяет, что текстовый файл корректно анализируется.
     * Подсчитываются частоты каждого символа.
     */
    @Test
    void testAnalyzeTextFile() throws IOException {
        Path textFile = tempDir.resolve("test.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(textFile.toFile()), StandardCharsets.UTF_8))) {
            writer.write("aabbc");
        }
        FrequencyAnalyzer analyzer = new FrequencyAnalyzer();
        analyzer.analyzeFile(textFile.toString());
        Map<Object, Integer> freq = analyzer.getCharacterFrequency();
        assertEquals(2, freq.get('a'));
        assertEquals(2, freq.get('b'));
        assertEquals(1, freq.get('c'));
        assertEquals(5, analyzer.getTotalCharacters());
    }

    /**
     * Проверяет, что бинарный файл корректно анализируется.
     * Подсчитываются частоты каждого байта.
     */
    @Test
    void testAnalyzeBinaryFile() throws IOException {
        Path binFile = tempDir.resolve("test.bin");
        try (FileOutputStream fos = new FileOutputStream(binFile.toFile())) {
            fos.write(new byte[]{0x00, 0x00, 0x01, 0x02, 0x02, 0x02});
        }
        FrequencyAnalyzer analyzer = new FrequencyAnalyzer();
        analyzer.analyzeFile(binFile.toString());
        Map<Object, Integer> freq = analyzer.getCharacterFrequency();
        for (Map.Entry<Object, Integer> entry : freq.entrySet()) {
            if (entry.getKey() instanceof Byte) {
                byte key = (Byte) entry.getKey();
                int value = entry.getValue();
                if (key == 0x00) {
                    assertEquals(2, value);
                } else if (key == 0x01) {
                    assertEquals(1, value);
                } else if (key == 0x02) {
                    assertEquals(3, value);
                }
            }
        }
        assertEquals(6, analyzer.getTotalCharacters());
    }

    /**
     * Проверяет, что пустой файл корректно анализируется
     */
    @Test
    void testAnalyzeEmptyFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        emptyFile.toFile().createNewFile();
        FrequencyAnalyzer analyzer = new FrequencyAnalyzer();
        analyzer.analyzeFile(emptyFile.toString());
        assertTrue(analyzer.getCharacterFrequency().isEmpty());
        assertEquals(0, analyzer.getTotalCharacters());
    }

    /**
     * Проверяет, что метод clear() корректно очищает собранную статистику
     */
    @Test
    void testClear() throws IOException {
        Path textFile = tempDir.resolve("test.txt");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(textFile.toFile()), StandardCharsets.UTF_8))) {
            writer.write("abc");
        }
        FrequencyAnalyzer analyzer = new FrequencyAnalyzer();
        analyzer.analyzeFile(textFile.toString());
        assertFalse(analyzer.getCharacterFrequency().isEmpty());
        analyzer.clear();
        assertTrue(analyzer.getCharacterFrequency().isEmpty());
        assertEquals(0, analyzer.getTotalCharacters());
    }
}