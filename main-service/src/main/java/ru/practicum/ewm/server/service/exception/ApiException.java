package ru.practicum.ewm.server.service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ApiException {
    private final List<String> errors;
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}
