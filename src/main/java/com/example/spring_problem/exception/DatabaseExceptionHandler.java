package com.example.spring_problem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Класс для обработки исключений {@link MissedDataException} и {@link TimeToLeaveException}
 */
@ControllerAdvice
public class DatabaseExceptionHandler {

    /**
     * Обрабатывает исключение MissingDataException и возвращает содержащееся в нём сообщение и соответствующий
     * HTTP статус
     * @param e исключение класса {@link MissedDataException}
     * @return ResponseEntity, содержащее сообщение и HttpStatus.NOT_FOUND
     */
    @ExceptionHandler
    public ResponseEntity<ReturnEMessage> handleDataException(MissedDataException e) {
        ReturnEMessage message = new ReturnEMessage();
        message.setMessage(e.getMessage());

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключение TimeToLeaveException и возвращает содержащееся в нём сообщение и соответствующий
     * HTTP статус
     * @param e исключение класса {@link TimeToLeaveException}
     * @return ResponseEntity, содержащее сообщение и HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler
    public ResponseEntity<ReturnEMessage> handleDataException(TimeToLeaveException e) {
        ReturnEMessage message = new ReturnEMessage();
        message.setMessage(e.getMessage());

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

}
