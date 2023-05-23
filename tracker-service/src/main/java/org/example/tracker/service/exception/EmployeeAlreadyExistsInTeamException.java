package org.example.tracker.service.exception;

public class EmployeeAlreadyExistsInTeamException extends RuntimeException {
    public EmployeeAlreadyExistsInTeamException() {
    }

    public EmployeeAlreadyExistsInTeamException(String message) {
        super(message);
    }
}
