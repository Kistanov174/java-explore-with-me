package ru.practicum.mainservice.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String objectName, Long id) {
        super(String.format("%s with id=%d was not found.", objectName, id));
    }
}