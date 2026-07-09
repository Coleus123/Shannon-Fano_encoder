package com.shannonfano.codec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для кодировщика битовых строк
 */
class BitStringEncoderTest {

    private BitStringEncoder encoder;
    private Map<Object, String> encodingTable;

    @BeforeEach
    void setUp() {
        encoder = new BitStringEncoder();
        encodingTable = new HashMap<>();
        encodingTable.put('a', "0");
        encodingTable.put('b', "10");
        encodingTable.put('c', "110");
        encodingTable.put('d', "111");
    }

    /**
     * Проверяет, что текстовое содержимое корректно кодируется в битовую строку
     */
    @Test
    void testEncodeText() {
        String content = "abcd";
        String expected = "010110111";
        assertEquals(expected, encoder.encode(content, encodingTable));
    }

    /**
     * Проверяет, что бинарное содержимое корректно кодируется в битовую строку
     */
    @Test
    void testEncodeBinary() {
        byte[] content = {0x00, 0x01, 0x02};
        Map<Object, String> table = new HashMap<>();
        table.put((byte) 0x00, "0");
        table.put((byte) 0x01, "10");
        table.put((byte) 0x02, "110");

        String expected = "010110";
        assertEquals(expected, encoder.encode(content, table));
    }

    /**
     * Проверяет, что пустая строка корректно кодируется в пустую битовую строку
     */
    @Test
    void testEncodeEmptyText() {
        String content = "";
        assertEquals("", encoder.encode(content, encodingTable));
    }

    /**
     * Проверяет, что пустой байтовый массив корректно кодируется в пустую битовую строку
     */
    @Test
    void testEncodeEmptyBinary() {
        byte[] content = new byte[0];
        assertEquals("", encoder.encode(content, encodingTable));
    }
}