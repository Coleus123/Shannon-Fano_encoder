package com.shannonfano.exception;

/**
 * Базовое исключение для операций с архивом
 */
public class ArchiveException extends Exception {

    /**
     * Создает исключение с указанным сообщением
     *
     * @param message Сообщение об ошибке
     */
    public ArchiveException(String message) {
        super(message);
    }
}