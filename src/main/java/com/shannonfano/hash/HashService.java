package com.shannonfano.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Сервис для вычисления хешей SHA-256,
 * используется для контроля целостности данных
 */
public class HashService {

    /**
     * Вычисляет SHA-256 хеш для строки
     *
     * @param content Строка для хеширования
     * @return Строковое представление хеша в шестнадцатеричном формате
     */
    public String calculateHash(String content) {
        if (content == null) {
            throw new NullPointerException("Содержимое не может быть null");
        }
        return calculateHash(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Вычисляет SHA-256 хеш для байтового массива
     *
     * @param content Байтовый массив для хеширования
     * @return Строковое представление хеша в шестнадцатеричном формате
     */
    public String calculateHash(byte[] content) {
        if (content == null) {
            throw new NullPointerException("Содержимое не может быть null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка SHA-256", e);
        }
    }
}