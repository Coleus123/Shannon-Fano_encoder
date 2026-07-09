package com.shannonfano.core;

import com.shannonfano.file.FileHandler;
import com.shannonfano.file.FileTypeDetector;
import com.shannonfano.file.TextFileHandler;
import com.shannonfano.file.BinaryFileHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Анализирует частоты символов в файле
 */
public class FrequencyAnalyzer {

    private final Map<Object, Integer> characterFrequency = new HashMap<>();
    private int totalCharacters = 0;
    private final FileTypeDetector typeDetector = new FileTypeDetector();

    /**
     * Анализирует файл для сбора статистики частот символов.
     * Для текстовых файлов анализируются символы char, для бинарных - байты
     *
     * @param filePath Путь к файлу
     */
    public void analyzeFile(String filePath) throws IOException {
        FileHandler handler = getHandler(filePath);
        Object content = handler.read(filePath);
        if (content instanceof String) {
            for (char c : ((String) content).toCharArray()) {
                characterFrequency.merge(c, 1, Integer::sum);
                totalCharacters++;
            }
        } else {
            for (byte b : (byte[]) content) {
                characterFrequency.merge(b, 1, Integer::sum);
                totalCharacters++;
            }
        }
    }

    /**
     * Рекурсивно анализирует директорию для сбора статистики частот
     *
     * @param dirPath Путь к директории
     */
    public void analyzeDirectory(String dirPath) throws IOException {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    analyzeFile(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    analyzeDirectory(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Возвращает обработчик для файла в зависимости от его типа
     *
     * @param filePath Путь к файлу
     * @return Обработчик файла
     */
    private FileHandler getHandler(String filePath) {
        if (typeDetector.isTextFile(filePath)) {
            return new TextFileHandler();
        } else {
            return new BinaryFileHandler();
        }
    }

    /**
     * Возвращает словарь частот символов
     *
     * @return Словарь частот
     */
    public Map<Object, Integer> getCharacterFrequency() {
        return new HashMap<>(characterFrequency);
    }

    /**
     * Возвращает общее количество символов
     *
     * @return Общее количество символов
     */
    public int getTotalCharacters() {
        return totalCharacters;
    }

    /**
     * Очищает собранную статистику
     */
    public void clear() {
        characterFrequency.clear();
        totalCharacters = 0;
    }
}