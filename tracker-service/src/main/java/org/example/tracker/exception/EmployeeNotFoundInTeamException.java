package org.example.tracker.exception;

public class EmployeeNotFoundInTeamException extends RuntimeException {
    public EmployeeNotFoundInTeamException() {
    }

    public EmployeeNotFoundInTeamException(String message) {
        super(message);
    }
}
