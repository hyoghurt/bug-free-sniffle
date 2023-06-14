package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.dto.project.*;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.entity.EmployeeEntity;
import org.example.tracker.entity.ProjectEntity;
import org.example.tracker.entity.TeamEmbeddable;
import org.example.tracker.exception.*;
import org.example.tracker.mapper.ModelMapper;
import org.example.tracker.repository.ProjectRepository;
import org.example.tracker.service.ProjectService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.tracker.repository.specification.ProfileSpecs.byFilterParam;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;

    @Override
    public ProjectResp create(ProjectReq request) {
        log.info("create: {}", request);
        ProjectEntity entity = new ProjectEntity();
        mergeRequestToEntity(request, entity);
        save(entity);
        return modelMapper.toProjectResp(entity);
    }

    @Override
    public ProjectResp update(Integer id, ProjectReq request) {
        log.info("update id: {} body: {}", id, request);
        ProjectEntity entity = getProjectEntity(id);
        mergeRequestToEntity(request, entity);
        save(entity);
        return modelMapper.toProjectResp(entity);
    }

    private void mergeRequestToEntity(ProjectReq request, ProjectEntity entity) {
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
    }

    private void save(ProjectEntity entity) {
        try {
            projectRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("projects_code_key")) {
                log.info("duplicate code: {}", entity.getCode());
                throw new DuplicateUniqueFieldException("project duplicate code " + entity.getCode());
            } else if (e.getMessage().contains("null value in column")) {
                throw new RequiredFieldException();
            }
            throw e;
        }
    }

    @Override
    public List<ProjectResp> getAllByParam(ProjectFilterParam param) {
        log.info("get all by param: {}", param);
        return projectRepository.findAll(byFilterParam(param))
                .stream()
                .map(modelMapper::toProjectResp)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, ProjectUpdateStatusReq request) {
        log.info("update status id: {} status: {}", id, request);
        ProjectEntity entity = getProjectEntity(id);
        checkProjectStatusFlow(entity.getStatus(), request.getStatus());
        entity.setStatus(request.getStatus());
    }

    private void checkProjectStatusFlow(ProjectStatus currentStatus, ProjectStatus newStatus) {
        if (newStatus.ordinal() < currentStatus.ordinal()) {
            log.info("project status incorrect flow update {} -> {}", currentStatus.name(), newStatus.name());
            throw new ProjectStatusIncorrectFlowUpdateException(
                    String.format("project status incorrect flow update: %s -> %s",
                            currentStatus.name(), newStatus.name())
            );
        }
    }

    @Override
    @Transactional
    public void addEmployee(Integer projectId, TeamEmbeddable teamEmbeddable) {
        log.info("add employee {} in project {}", teamEmbeddable, projectId);
        ProjectEntity entity = getProjectEntity(projectId);
        checkUniqueRoleAndEmployee(entity, teamEmbeddable);
        entity.getTeams().add(teamEmbeddable);
    }

    private void checkUniqueRoleAndEmployee(ProjectEntity projectEntity, TeamEmbeddable teamEmbeddable) {
        Set<TeamEmbeddable> teams = projectEntity.getTeams();
        Integer projectId = projectEntity.getId();
        for (TeamEmbeddable emb : teams) {
            EmployeeRole role = teamEmbeddable.getRole();
            EmployeeEntity employee = teamEmbeddable.getEmployee();
            if (emb.getRole().equals(role)) {
                log.info("role {} already exists in team {}", role, projectId);
                throw new RoleAlreadyExistsInTeamException("role already exists - " + role.name());
            }
            if (emb.getEmployee().equals(employee)) {
                log.info("employee {} already exists in team {}", employee.getId(), projectId);
                throw new EmployeeAlreadyExistsInTeamException("employee already exists - " + employee.getId());
            }
        }
    }

    @Override
    @Transactional
    public void removeEmployee(Integer projectId, Integer employeeId) {
        log.info("remove employee {} from project {}", employeeId, projectId);
        ProjectEntity projectEntity = getProjectEntity(projectId);

        // находим сотрудика в проекте
        TeamEmbeddable teamEmbeddable = projectEntity.getTeams().stream()
                .filter(team -> team.getEmployee().getId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("project team not found employee " + employeeId));

        projectEntity.getTeams().remove(teamEmbeddable);
    }

    @Override
    @Transactional
    public List<TeamResp> getAllTeamEmployee(Integer projectId) {
        log.info("get team {}", projectId);
        return getProjectEntity(projectId).getTeams()
                .stream()
                .map(modelMapper::toTeamResp)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectEntity getProjectEntity(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("project not found " + id));
    }

    @Override
    public boolean isInTeam(ProjectEntity projectEntity, Integer employeeId) {
        return projectEntity.getTeams().stream()
                .anyMatch(t -> t.getEmployee().getId().equals(employeeId));
    }

    @Override
    public boolean isInTeam(ProjectEntity projectEntity, String upn) {
        return projectEntity.getTeams().stream()
                .anyMatch(t -> t.getEmployee().getUpn().equals(upn));
    }
}
