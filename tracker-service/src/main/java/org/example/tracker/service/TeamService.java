package org.example.tracker.service;

import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.team.TeamReq;

import java.util.List;

public interface TeamService {
    void addEmployeeToProject(TeamReq request);
    void removeEmployeeFromProject(Integer projectId, Integer employeeId);
    List<EmployeeResp> getProjectEmployee(Integer projectId);
}
