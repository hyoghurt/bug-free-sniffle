package org.example.tracker.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "статус сотрудника", example = "ACTIVE")
public enum EmployeeStatus {
    ACTIVE,
    DELETED
}
