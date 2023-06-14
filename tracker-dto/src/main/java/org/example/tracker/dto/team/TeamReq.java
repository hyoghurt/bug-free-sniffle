package org.example.tracker.dto.team;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamReq {

    @Schema(description = "уникальный идентификатор сотрудника", example = "1")
    @NotNull(message = "employeeId required")
    private Integer employeeId;

    @NotNull(message = "role required")
    private EmployeeRole role;
}
