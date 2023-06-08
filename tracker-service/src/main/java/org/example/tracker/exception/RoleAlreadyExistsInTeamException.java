package org.example.tracker.exception;

public class RoleAlreadyExistsInTeamException extends RuntimeException {
    public RoleAlreadyExistsInTeamException() {
    }

    public RoleAlreadyExistsInTeamException(String message) {
        super(message);
    }
}
