package ru.practicum.mainservice.exception;

public class EventDateException extends RuntimeException {
    public EventDateException(int hours, String momentDescription) {
        super(String.format("Event date must not be earlier than %d hour(s) after moment of %s", hours, momentDescription));
    }
}