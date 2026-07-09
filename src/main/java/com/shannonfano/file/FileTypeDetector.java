package com.shannonfano.file;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Определяет тип файла
 */
public class FileTypeDetector {

    /**
     * Пытается определить, является ли файл текстовым
     *
     * @param filePath Путь к файлу
     * @return true, если файл текстовый, иначе false
     */
    public boolean isTextFile(String filePath) {
        try (InputStream is = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead = is.read(buffer);
            if (bytesRead == -1) {
                return true;
            }
            String test = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            byte[] testBytes = test.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < bytesRead; i++) {
                if (i < testBytes.length && buffer[i] != testBytes[i]) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}