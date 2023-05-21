package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.TeamEmbeddable;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.TeamService;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final ModelMapper modelMapper;
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    @Override
    public void addEmployeeToProject(TeamReq request) {
        EmployeeEntity employeeEntity = employeeService.getEmployeeEntity(request.getEmployeeId());
        TeamEmbeddable teamEmbeddable = modelMapper.toTeamEmbeddable(employeeEntity, request.getRole());
        projectService.addEmployee(request.getProjectId(), teamEmbeddable);
    }

    @Override
    public void removeEmployeeFromProject(Integer projectId, Integer employeeId) {
        projectService.removeEmployee(projectId, employeeId);
    }

    @Override
    public List<EmployeeResp> getProjectEmployee(Integer projectId) {
        return projectService.getAllEmployee(projectId);
    }
}
