package com.shannonfano.codec;

import com.shannonfano.exception.CorruptArchiveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для декодировщика битовых строк
 */
class BitStringDecoderTest {

    private BitStringDecoder decoder;
    private Map<String, Object> decodingTable;

    @BeforeEach
    void setUp() {
        decoder = new BitStringDecoder();
        decodingTable = new HashMap<>();
        decodingTable.put("0", 'a');
        decodingTable.put("10", 'b');
        decodingTable.put("110", 'c');
        decodingTable.put("111", 'd');
    }

    /**
     * Проверяет, что битовая строка корректно декодируется в текстовое содержимое
     */
    @Test
    void testDecodeText() throws CorruptArchiveException {
        String bitString = "010110111";
        String expected = "abcd";
        assertEquals(expected, decoder.decodeText(bitString, decodingTable));
    }

    /**
     * Проверяет, что битовая строка корректно декодируется в бинарное содержимое
     */
    @Test
    void testDecodeBinary() throws CorruptArchiveException {
        Map<String, Object> table = new HashMap<>();
        table.put("0", (byte) 0x00);
        table.put("10", (byte) 0x01);
        table.put("110", (byte) 0x02);

        String bitString = "010110";
        byte[] expected = {0x00, 0x01, 0x02};
        assertArrayEquals(expected, decoder.decodeBinary(bitString, table));
    }

    /**
     * Проверяет, что пустая битовая строка корректно декодируется в пустую строку
     */
    @Test
    void testDecodeEmptyText() throws CorruptArchiveException {
        assertEquals("", decoder.decodeText("", decodingTable));
    }

    /**
     * Проверяет, что пустая битовая строка корректно декодируется в пустой байтовый массив
     */
    @Test
    void testDecodeEmptyBinary() throws CorruptArchiveException {
        assertArrayEquals(new byte[0], decoder.decodeBinary("", decodingTable));
    }

    /**
     * Проверяет, что при наличии
     * необработанных бит выбрасывается исключение CorruptArchiveException
     */
    @Test
    void testDecodeWithRemainingBits() {
        String bitString = "0101101111";
        assertThrows(CorruptArchiveException.class,
                () -> decoder.decodeText(bitString, decodingTable));
    }

    /**
     * Проверяет, что байтовый массив корректно преобразуется в битовую строку
     */
    @Test
    void testBytesToBitString() {
        byte[] bytes = {0x01, 0x02, 0x03};
        int bitCount = 16;
        String expected = "0000000100000010";
        assertEquals(expected, decoder.bytesToBitString(bytes, bitCount));
    }

    /**
     * Проверяет, что байтовый массив с крайними
     * значениями корректно преобразуется в битовую строку
     */
    @Test
    void testBytesToBitStringFull() {
        byte[] bytes = {(byte) 0xFF, (byte) 0x00};
        int bitCount = 16;
        String expected = "1111111100000000";
        assertEquals(expected, decoder.bytesToBitString(bytes, bitCount));
    }
}