package com.shannonfano.core;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для построения таблиц кодирования.
 */
class CodeTableBuilderTest {

    /**
     * Проверяет, что таблицы кодирования строятся корректно для четырех символов.
     * Проверяет размер таблиц и обратное преобразование.
     */
    @Test
    void testBuildTables() {
        Map<Object, Integer> frequency = new HashMap<>();
        frequency.put('a', 5);
        frequency.put('b', 3);
        frequency.put('c', 2);
        frequency.put('d', 1);
        CodeTableBuilder builder = new CodeTableBuilder();
        builder.buildTables(frequency);
        Map<Object, String> encodingTable = builder.getEncodingTable();
        Map<String, Object> decodingTable = builder.getDecodingTable();
        assertEquals(4, encodingTable.size());
        assertEquals(4, decodingTable.size());
        for (String code : encodingTable.values()) {
            assertTrue(code.length() >= 1 && code.length() <= 3);
        }
        for (Map.Entry<Object, String> entry : encodingTable.entrySet()) {
            assertEquals(entry.getKey(), decodingTable.get(entry.getValue()));
        }
    }

    /**
     * Проверяет, что при пустой карте частот таблицы кодирования остаются пустыми
     */
    @Test
    void testBuildTablesWithEmptyFrequency() {
        Map<Object, Integer> frequency = new HashMap<>();
        CodeTableBuilder builder = new CodeTableBuilder();
        builder.buildTables(frequency);
        assertTrue(builder.getEncodingTable().isEmpty());
        assertTrue(builder.getDecodingTable().isEmpty());
    }

    /**
     * Проверяет, что при одном символе он получает код "0"
     */
    @Test
    void testBuildTablesWithOneSymbol() {
        Map<Object, Integer> frequency = new HashMap<>();
        frequency.put('a', 10);
        CodeTableBuilder builder = new CodeTableBuilder();
        builder.buildTables(frequency);
        Map<Object, String> encodingTable = builder.getEncodingTable();
        assertEquals(1, encodingTable.size());
        assertEquals("0", encodingTable.get('a'));
    }

    /**
     * Проверяет, что при двух символах они получают коды "0" и "1"
     */
    @Test
    void testBuildTablesWithTwoSymbols() {
        Map<Object, Integer> frequency = new HashMap<>();
        frequency.put('a', 5);
        frequency.put('b', 3);
        CodeTableBuilder builder = new CodeTableBuilder();
        builder.buildTables(frequency);
        Map<Object, String> encodingTable = builder.getEncodingTable();
        assertEquals(2, encodingTable.size());
        assertTrue(encodingTable.get('a').equals("0") || encodingTable.get('a').equals("1"));
        assertTrue(encodingTable.get('b').equals("0") || encodingTable.get('b').equals("1"));
        assertNotEquals(encodingTable.get('a'), encodingTable.get('b'));
    }
}