package org.example.tracker.service.exception;

public class EmployeeNotFoundInTeamException extends RuntimeException {
    public EmployeeNotFoundInTeamException() {
    }

    public EmployeeNotFoundInTeamException(String message) {
        super(message);
    }
}
