package org.example.tracker.service;

import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.dto.team.TeamResp;

import java.util.List;

public interface TeamService {
    void addEmployeeToProject(Integer projectId, TeamReq request);
    void removeEmployeeFromProject(Integer projectId, Integer employeeId);
    List<TeamResp> getProjectEmployees(Integer projectId);
}
