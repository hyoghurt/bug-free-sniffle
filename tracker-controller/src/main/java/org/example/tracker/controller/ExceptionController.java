package org.example.tracker.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.dto.error.ErrorResp;
import org.example.tracker.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Hidden
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResp> problem(final Throwable e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Problem occurred";
        UUID uuid = UUID.randomUUID();
        String logRef = uuid.toString();
        log.error("logRef={} {} {}", logRef, message, e);
        return ResponseEntity
                .status(status)
                .body(ErrorResp.builder()
                        .message(logRef + " : " + message)
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResp> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
        List<String> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        fieldErrors.forEach(fieldError -> errors
                .add(fieldError.getField() + ", " + fieldError.getDefaultMessage()));
        globalErrors.forEach(objectError -> errors
                .add(objectError.getObjectName() + ", " + objectError.getDefaultMessage()));

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
            HttpMessageNotReadableException.class, // error parse json to java type
            MethodArgumentTypeMismatchException.class, // endpoint path incorrect type
            TaskStatusIncorrectFlowUpdateException.class,
            EmployeeNotFoundInTeamException.class,
            EmployeeAlreadyDeletedException.class,
            EmployeeAlreadyExistsInTeamException.class,
            DuplicateUniqueFieldException.class,
            RequiredFieldException.class,
            ProjectStatusIncorrectFlowUpdateException.class,
            RoleAlreadyExistsInTeamException.class,
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
