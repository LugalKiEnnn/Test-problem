package com.example.spring_problem.exception;

/**
 * Класс, хранящий сообщение, полученное из исключения
 */
public class ReturnEMessage {

    private String message;

    /**
     * Метод, возвращающий хранящийся текст
     * @return возвращает текст сообщения
     */
    public String getMessage() {
        return message;
    }

    /**
     * Метод, присваивающий текст
     * @param message текстовое сообщение
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
