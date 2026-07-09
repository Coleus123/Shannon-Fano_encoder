package com.shannonfano.codec;

import java.util.Map;

/**
 * Кодирует содержимое в битовую строку с использованием таблицы кодирования
 */
public class BitStringEncoder {

    /**
     * Кодирует содержимое в битовую строку,
     * каждый символ или байт заменяется на соответствующий битовый код из таблицы.
     *
     * @param content Содержимое для кодирования
     * @param encodingTable Таблица кодирования
     * @return Закодированная битовая строка
     */
    public String encode(Object content, Map<Object, String> encodingTable) {
        StringBuilder bitString = new StringBuilder();
        if (content instanceof String) {
            for (char c : ((String) content).toCharArray()) {
                bitString.append(encodingTable.get(c));
            }
        } else {
            for (byte b : (byte[]) content) {
                bitString.append(encodingTable.get(b));
            }
        }
        return bitString.toString();
    }
}