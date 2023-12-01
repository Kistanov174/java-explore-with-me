package ru.practicum.mainservice.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventDateConstraintValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EventCreationDate {
    String message() default "{Event date must be not earlier than 2 hours after current moment}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}