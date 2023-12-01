package ru.practicum.mainservice.exception;

public class UserAccessRightsException extends RuntimeException {
    public UserAccessRightsException() {
        super("Only initiator can change event parameters");
    }
}