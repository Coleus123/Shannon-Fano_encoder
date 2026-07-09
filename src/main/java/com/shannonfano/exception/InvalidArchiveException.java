package com.shannonfano.exception;

/**
 * Исключение, выбрасываемое при неверном формате архива.
 * Возникает, когда сигнатура архива не соответствует ожидаемой.
 */
public class InvalidArchiveException extends ArchiveException {

    /**
     * Создает исключение с указанным сообщением
     *
     * @param message Сообщение об ошибке
     */
    public InvalidArchiveException(String message) {
        super(message);
    }
}