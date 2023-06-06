package org.example.tracker.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "роли сотрудника в команде", example = "TESTER")
public enum EmployeeRole {
    ANALYST,
    DEVELOPER,
    TESTER,
    PROJECT_MANAGER
}
