package org.example.tracker.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.tracker.annotation.DeadlineValid;
import org.example.tracker.dto.task.TaskReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DeadlineValidator implements ConstraintValidator<DeadlineValid, TaskReq> {
    private static final Logger logger = LoggerFactory.getLogger(DeadlineValidator.class);

    @Override
    public void initialize(DeadlineValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(TaskReq taskReq, ConstraintValidatorContext constraintValidatorContext) {
        Instant current = Instant.now();
        Integer hours = taskReq.getLaborCostsInHours();
        Instant deadline = taskReq.getDeadlineDatetime();
        if (hours == null || deadline == null) return false;
        logger.info(current + " + " + hours + " = " + current.plus(hours, ChronoUnit.HOURS) + ", deadline: " + deadline);
        return deadline.isAfter(Instant.now().plus(hours, ChronoUnit.HOURS));
    }
}
