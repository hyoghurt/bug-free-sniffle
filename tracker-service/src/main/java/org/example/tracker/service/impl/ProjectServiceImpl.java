package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TeamEmbeddable;
import org.example.tracker.dao.repository.ProjectRepository;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.project.*;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.exception.EmployeeNotFoundException;
import org.example.tracker.service.exception.ProjectNotFoundException;
import org.example.tracker.service.exception.ProjectStatusIncorrectFlowUpdateException;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;

    @Override
    public void create(ProjectReq request) {
        ProjectEntity entity = modelMapper.toProjectEntity(request);
        projectRepository.save(entity);
    }

    @Override
    public void update(Integer id, ProjectReq request) {
        ProjectEntity entity = modelMapper.toProjectEntity(request);
        entity.setId(id);
        projectRepository.save(entity);
    }

    @Override
    public List<ProjectResp> findByParam(ProjectFilterParam param) {
        List<ProjectEntity> entities = projectRepository.findByFilter(param);
        return entities.stream()
                .map(modelMapper::toProjectResp)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, ProjectUpdateStatusReq request) {
        ProjectEntity entity = getProjectEntity(id);
        if (request.getStatus().ordinal() < entity.getStatus().ordinal()) {
            throw new ProjectStatusIncorrectFlowUpdateException(String
                    .format("project status incorrect flow update: %s -> %s",
                            entity.getStatus().name(),
                            request.getStatus().name()));
        }
        entity.setStatus(request.getStatus());
    }

    public ProjectEntity getProjectEntity(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("project not found " + id));
    }

    @Override
    public void addEmployee(Integer projectId, TeamEmbeddable team) {
        ProjectEntity entity = getProjectEntity(projectId);
        //TODO check role
        entity.getTeams().add(team);
    }

    @Override
    public void removeEmployee(Integer projectId, Integer employeeId) {
        ProjectEntity entity = getProjectEntity(projectId);
        TeamEmbeddable teamEmbeddable = entity.getTeams().stream()
                .filter(team -> team.getEmployee().getId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("project team not found employee " + employeeId));
        entity.getTeams().remove(teamEmbeddable);
    }

    @Override
    public List<EmployeeResp> getAllEmployee(Integer projectId) {
        ProjectEntity entity = getProjectEntity(projectId);
        return entity.getTeams().stream()
                .map(team -> modelMapper.toEmployeeResp(team.getEmployee()))
                .collect(Collectors.toList());
    }
}
