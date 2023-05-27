package org.example.tracker.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.example.tracker.dto.error.ErrorResp;
import org.example.tracker.service.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Hidden
@RestControllerAdvice
public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Problem> problem(final Throwable e) {
        String message = "Problem occured";
        UUID uuid = UUID.randomUUID();
        String logRef = uuid.toString();
        logger.error("logRef=" + logRef, message, e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        return new ResponseEntity<Problem>(new Problem(logRef, message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResp> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        List<String> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        fieldErrors.forEach(fieldError -> {
            errors.add(fieldError.getField() + ", " + fieldError.getDefaultMessage());
        });
        globalErrors.forEach(objectError -> {
            errors.add(objectError.getObjectName() + ", " + objectError.getDefaultMessage());
        });

        return ResponseEntity
                .status(status)
                .body(ErrorResp.builder()
                        .message(status.getReasonPhrase())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler({
            EmployeeNotFoundException.class,
            ProjectNotFoundException.class,
            TaskNotFoundException.class
    })
    public ResponseEntity<ErrorResp> handleNotFound(RuntimeException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(status)
                .body(ErrorResp.builder()
                        .message(ex.getMessage())
                        .build()
                );
    }

    @ExceptionHandler({
            EmployeeNotFoundInTeamException.class,
            EmployeeAlreadyDeletedException.class,
            EmployeeAlreadyExistsInTeamException.class,
            DuplicateUniqueFieldException.class,
            RequiredFieldException.class,
            ProjectStatusIncorrectFlowUpdateException.class,
            RoleAlreadyExistsInTeamException.class
    })
    public ResponseEntity<ErrorResp> handleBadRequest(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(status)
                .body(ErrorResp.builder()
                        .message(ex.getMessage())
                        .build()
                );
    }
}
