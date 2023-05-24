package org.example.tracker.dto.team;


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
    @NotNull
    private Integer employeeId;
    @NotNull
    private EmployeeRole role;
}
