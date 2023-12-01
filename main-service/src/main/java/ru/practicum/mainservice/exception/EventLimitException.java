package ru.practicum.mainservice.exception;

public class EventLimitException extends RuntimeException {
    public EventLimitException() {
        super("The participants limit has been reached. Cannot proceed request.");
    }
}