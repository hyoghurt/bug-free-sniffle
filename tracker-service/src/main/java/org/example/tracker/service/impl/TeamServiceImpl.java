package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.TeamService;
import org.example.tracker.service.exception.EmployeeAlreadyDeletedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    @Override
    public void addEmployeeToProject(Integer projectId, TeamReq request) {
        EmployeeEntity employeeEntity = employeeService.getEmployeeEntity(request.getEmployeeId());
        if (employeeService.isDeleted(employeeEntity)) {
            throw new EmployeeAlreadyDeletedException("employee already deleted " + employeeEntity.getId());
        }
        projectService.addEmployee(projectId, employeeEntity, request.getRole());
    }

    @Override
    public void removeEmployeeFromProject(Integer projectId, Integer employeeId) {
        projectService.removeEmployee(projectId, employeeId);
    }

    @Override
    public List<TeamResp> getProjectEmployees(Integer projectId) {
        return projectService.getAllTeamEmployee(projectId);
    }
}
