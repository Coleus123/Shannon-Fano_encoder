package com.shannonfano.exception;

/**
 * Исключение, выбрасываемое при повреждении архива.
 * Возникает при несовпадении контрольных сумм или при наличии необработанных бит.
 */
public class CorruptArchiveException extends ArchiveException {

    /**
     * Создает исключение с указанным сообщением
     *
     * @param message Сообщение об ошибке
     */
    public CorruptArchiveException(String message) {
        super(message);
    }
}