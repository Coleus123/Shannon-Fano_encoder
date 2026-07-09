package com.shannonfano.archive;

import java.nio.charset.StandardCharsets;

/**
 * Метаданные архива: сигнатура и типы элементов.
 * Содержит константы, используемые при формировании архива.
 */
public class ArchiveMetadata {

    public static final byte[] SIGNATURE = "SFAR".getBytes(StandardCharsets.US_ASCII);
    public static final byte[] FILE = "FILE".getBytes(StandardCharsets.US_ASCII);
    public static final byte[] TEXT = "TEXT".getBytes(StandardCharsets.US_ASCII);
    public static final byte[] BINARY = "BINARY".getBytes(StandardCharsets.US_ASCII);

    /**
     * Приватный конструктор для класса-констант.
     * Запрещает создание экземпляров.
     */
    private ArchiveMetadata() {}
}