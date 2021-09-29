package com.example.spring_problem.exception;

/**
 * Исключение, даёт знать о том, что переданное значение ttl не удовлетворяет требованиям
 */
public class TimeToLeaveException extends IllegalArgumentException {
    /**
     * Создаёт новое исключение, передающее сообщение о том, что ttl нужно передавать положительное
     */
    public TimeToLeaveException() {
        super("Ttl must be positive");
    }
}
