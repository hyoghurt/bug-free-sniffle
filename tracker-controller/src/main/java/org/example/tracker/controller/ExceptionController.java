package org.example.tracker.controller;

import org.example.tracker.dto.error.ErrorResp;
import org.example.tracker.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

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
