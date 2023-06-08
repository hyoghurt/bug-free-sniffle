package org.example.tracker.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.tracker.validator.DeadlineValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = DeadlineValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface DeadlineValid {

    String message() default "deadline not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
