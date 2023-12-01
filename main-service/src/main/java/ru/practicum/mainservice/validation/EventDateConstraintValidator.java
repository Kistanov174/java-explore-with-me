package ru.practicum.mainservice.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateConstraintValidator implements ConstraintValidator<EventCreationDate, LocalDateTime> {
    @Override
    public void initialize(EventCreationDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime creationDate, ConstraintValidatorContext constraintValidatorContext) {
        return creationDate.isAfter(LocalDateTime.now().plusHours(2));
    }
}