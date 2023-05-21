package org.example.tracker.dto.team;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamReq {
    private int projectId; //required
    private int employeeId; //required
    private EmployeeRole role; //required
}
