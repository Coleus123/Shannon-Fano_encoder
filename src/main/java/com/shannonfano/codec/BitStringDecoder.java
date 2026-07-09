package com.shannonfano.codec;

import com.shannonfano.exception.CorruptArchiveException;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Декодирует битовую строку обратно в содержимое с использованием таблицы декодирования
 */
public class BitStringDecoder {

    /**
     * Декодирует битовую строку в текстовое содержимое,
     * последовательно читает биты и находит соответствующие символы в таблице декодирования
     *
     * @param bitString Битовая строка для декодирования
     * @param decodingTable Таблица декодирования
     * @return Декодированная строка
     */
    public String decodeText(String bitString, Map<String, Object> decodingTable) throws CorruptArchiveException {
        StringBuilder currentCode = new StringBuilder();
        StringBuilder decodedText = new StringBuilder();
        for (char bit : bitString.toCharArray()) {
            currentCode.append(bit);
            if (decodingTable.containsKey(currentCode.toString())) {
                Object value = decodingTable.get(currentCode.toString());
                decodedText.append((char) value);
                currentCode = new StringBuilder();
            }
        }
        if (currentCode.length() > 0) {
            throw new CorruptArchiveException("Остались необработанные биты - файл поврежден");
        }
        return decodedText.toString();
    }

    /**
     * Декодирует битовую строку в бинарное содержимое,
     * последовательно читает биты и находит соответствующие байты в таблице декодирования.
     *
     * @param bitString Битовая строка для декодирования
     * @param decodingTable Таблица декодирования
     * @return Декодированный байтовый массив
     */
    public byte[] decodeBinary(String bitString, Map<String, Object> decodingTable) throws CorruptArchiveException {
        StringBuilder currentCode = new StringBuilder();
        ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream();
        for (char bit : bitString.toCharArray()) {
            currentCode.append(bit);
            if (decodingTable.containsKey(currentCode.toString())) {
                Object value = decodingTable.get(currentCode.toString());
                decodedBytes.write((Byte) value);
                currentCode = new StringBuilder();
            }
        }
        if (currentCode.length() > 0) {
            throw new CorruptArchiveException("Остались необработанные биты - файл поврежден");
        }
        return decodedBytes.toByteArray();
    }

    /**
     * Восстанавливает битовую строку из массива байт,
     * каждый байт преобразуется в 8-битную строку с ведущими нулями.
     *
     * @param bytes Массив байт для преобразования
     * @param bitCount Количество бит, которые нужно извлечь
     * @return Битовая строка заданной длины
     */
    public String bytesToBitString(byte[] bytes, int bitCount) {
        StringBuilder bitString = new StringBuilder();
        for (byte b : bytes) {
            String bits = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            bitString.append(bits);
        }
        return bitString.substring(0, bitCount);
    }
}