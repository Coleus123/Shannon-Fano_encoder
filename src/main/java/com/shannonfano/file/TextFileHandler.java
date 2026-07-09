package com.shannonfano.file;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Обработчик текстовых файлов
 */
public class TextFileHandler implements FileHandler<String> {

    /**
     * Читает текстовый файл и возвращает его содержимое в виде строки
     *
     * @param filePath Путь к файлу
     * @return Содержимое файла в виде строки
     */
    @Override
    public String read(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }
            if (content.length() > 0) {
                content.deleteCharAt(content.length() - 1);
            }
        }
        return content.toString();
    }

    /**
     * Записывает строковое содержимое в текстовый файл
     *
     * @param filePath Путь к файлу
     * @param content Содержимое для записи
     */
    @Override
    public void write(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }
}