package com.shannonfano.file;

import java.io.*;

/**
 * Обработчик бинарных файлов
 */
public class BinaryFileHandler implements FileHandler<byte[]> {

    private static final int BUFFER_SIZE = 8192;

    /**
     * Читает бинарный файл и возвращает его содержимое в виде байтового массива
     *
     * @param filePath Путь к файлу
     * @return Содержимое файла в виде байтового массива
     */
    @Override
    public byte[] read(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    /**
     * Записывает байтовое содержимое в бинарный файл
     *
     * @param filePath Путь к файлу
     * @param content Содержимое для записи
     */
    @Override
    public void write(String filePath, byte[] content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(content);
        }
    }
}