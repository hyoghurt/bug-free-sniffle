package org.example.tracker.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус проекта - текстовое значение, обозначающее состояние проекта.",
        example = "DRAFT")
public enum ProjectStatus {
    DRAFT,
    IN_DEVELOPMENT,
    IN_TESTING,
    FINISHED
}
