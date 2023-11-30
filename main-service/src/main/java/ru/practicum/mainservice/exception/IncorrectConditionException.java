package ru.practicum.mainservice.exception;

public class IncorrectConditionException extends RuntimeException {
    public IncorrectConditionException(String message) {
        super(message);
    }
}