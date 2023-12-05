package ru.practicum.ewm.server.service.exception;

public class IncorrectConditionException extends RuntimeException {
    public IncorrectConditionException(String message) {
        super(message);
    }
}