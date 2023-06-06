package org.example.tracker.dto.team;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tracker.dto.employee.EmployeeResp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResp {
    private EmployeeResp employee;
    private EmployeeRole role;
}
