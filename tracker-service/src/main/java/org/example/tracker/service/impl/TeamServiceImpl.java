package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.TeamService;
import org.example.tracker.service.exception.EmployeeAlreadyDeletedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    @Override
    public void addEmployeeToProject(Integer projectId, TeamReq request) {
        log.info("add employee {} {} in team {}", request.getEmployeeId(), request.getRole(), projectId);
        EmployeeEntity employeeEntity = employeeService.getEmployeeEntity(request.getEmployeeId());
        if (employeeService.isDeleted(employeeEntity)) {
            log.warn("employee {} is deleted", employeeEntity.getId());
            throw new EmployeeAlreadyDeletedException("employee already deleted " + employeeEntity.getId());
        }
        projectService.addEmployee(projectId, employeeEntity, request.getRole());
    }

    @Override
    public void removeEmployeeFromProject(Integer projectId, Integer employeeId) {
        log.info("remove employee {} from team {}", employeeId, projectId);
        projectService.removeEmployee(projectId, employeeId);
    }

    @Override
    public List<TeamResp> getProjectEmployees(Integer projectId) {
        log.info("get team {}", projectId);
        return projectService.getAllTeamEmployee(projectId);
    }
}
