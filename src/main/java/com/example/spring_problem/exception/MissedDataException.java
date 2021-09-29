package com.example.spring_problem.exception;

/**
 * Исключение, даёт знать о том, что переданного ключа нет в базе данных и операции с ним невозможны
 */
public class MissedDataException extends IllegalArgumentException {
    /**
     * Создаёт новое исключение, передающее сообщение о том, что переданный ключ отсутствует в базе данных
     */
    public MissedDataException() {
        super("There is no such key in database");
    }

}
