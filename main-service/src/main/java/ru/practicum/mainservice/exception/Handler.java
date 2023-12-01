package ru.practicum.mainservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class Handler {
    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .reason("The required object was not found.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.NOT_FOUND.name())
                .details(Arrays.asList(ex.getStackTrace()))
                .build();
        log.error("ExceptionHandler: Status: {}, Message: {}",error.getStatus(), error.getMessage());
        log.debug("ExceptionHandler: StackTrace: {}",error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RequestParametersException.class, EventStateException.class, EventDateException.class,
            UserAccessRightsException.class, EventLimitException.class})
    public final ResponseEntity<ErrorResponse> handleRequestParametersException(RequestParametersException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .reason("For the requested operation the conditions are not met.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.FORBIDDEN.name())
                .details(Arrays.asList(ex.getStackTrace()))
                .build();
        log.error("ExceptionHandler: Status: {}, Message: {}",error.getStatus(), error.getMessage());
        log.debug("ExceptionHandler: StackTrace: {}",error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse>  handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .reason("Required parameters are not valid")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.name())
                .details(Arrays.asList(ex.getStackTrace()))
                .build();
        log.error("ExceptionHandler: Status: {}, Message: {}",error.getStatus(), error.getMessage());
        log.debug("ExceptionHandler: StackTrace: {}",error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            org.hibernate.exception.ConstraintViolationException.class,
            javax.validation.ConstraintViolationException.class,
            DataIntegrityViolationException.class})
    public final ResponseEntity<ErrorResponse>  handleConflictException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .reason("Integrity constraint has been violated")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.CONFLICT.name())
                .details(Arrays.asList(ex.getStackTrace()))
                .build();
        log.error("ExceptionHandler: Status: {}, Message: {}",error.getStatus(), error.getMessage());
        log.debug("ExceptionHandler: StackTrace: {}",error.getDetails());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}