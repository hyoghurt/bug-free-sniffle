package org.example.tracker.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "статус задачи", example = "OPEN")
public enum TaskStatus {
    OPEN,
    IN_PROCESS,
    DONE,
    CLOSED
}
