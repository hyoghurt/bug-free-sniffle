package org.example.tracker.service.exception;

public class EmployeeIsDeletedException extends RuntimeException {
    public EmployeeIsDeletedException(String message) {
        super(message);
    }
}
