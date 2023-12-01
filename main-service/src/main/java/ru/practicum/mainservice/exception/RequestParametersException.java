package ru.practicum.mainservice.exception;

public class RequestParametersException extends RuntimeException {
    public RequestParametersException(final String message) {
        super(message);
    }
}