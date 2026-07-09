package com.shannonfano.file;

import java.io.IOException;

/**
 * Интерфейс для работы с файлами различных типов
 *
 * @param <T> Тип данных, с которыми работает обработчик
 */
public interface FileHandler<T> {

    /**
     * Читает содержимое файла
     *
     * @param filePath Путь к файлу
     * @return Содержимое файла типа T
     */
    T read(String filePath) throws IOException;

    /**
     * Записывает содержимое в файл
     *
     * @param filePath Путь к файлу
     * @param content Содержимое для записи типа T
     */
    void write(String filePath, T content) throws IOException;
}