package ru.practicum.mainservice.exception;

public class EventStateException extends RuntimeException {
    public EventStateException(String currentState) {
        super(String.format("Event state is %s. Events only in PENDING state can be published, cancelled or changed.", currentState));
    }
}