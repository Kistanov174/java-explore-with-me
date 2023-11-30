package ru.practicum.mainservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class Handler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiException handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage());

        return new ApiException(
                List.of(),
                "BAD_REQUEST",
                "Incorrectly made request.",
                e.getMessage(),
                getTimestampToString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiException handleNotValidException(final ValidationException e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage());

        return new ApiException(
                List.of(),
                "BAD_REQUEST",
                "Incorrectly made request.",
                e.getMessage(),
                getTimestampToString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiException handleConstraintViolationException(final ConstraintViolationException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage());

        return new ApiException(
                List.of(),
                "CONFLICT",
                "Integrity constraint has been violated.",
                e.getMessage(),
                getTimestampToString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiException handleConflictException(final ConflictException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage());

        return new ApiException(
                List.of(),
                "CONFLICT",
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                getTimestampToString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ApiException handleIncorrectConditionsException(final IncorrectConditionException e) {
        log.debug("Получен статус 403 Forbidden {}", e.getMessage());

        return new ApiException(
                List.of(),
                "FORBIDDEN",
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                getTimestampToString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiException handleNotFoundException(final DataNotFoundException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage());

        return new ApiException(
                List.of(),
                "NOT_FOUND",
                "The required object was not found.",
                e.getMessage(),
                getTimestampToString()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ApiException handleThrowable(final Throwable e) {
        log.debug("Получен статус 500 Internal Server Error {}", e.getMessage());

        return new ApiException(
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList()),
                "INTERNAL_SERVER_ERROR",
                "Server encountered an unexpected error",
                e.getMessage(),
                getTimestampToString()
        );
    }

    private String getTimestampToString() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
    }
}