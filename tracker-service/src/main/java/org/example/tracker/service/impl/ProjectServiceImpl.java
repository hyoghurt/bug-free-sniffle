package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TeamEmbeddable;
import org.example.tracker.dao.repository.ProjectRepository;
import org.example.tracker.dto.project.ProjectFilterParam;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectUpdateStatusReq;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.exception.*;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.tracker.dao.specification.ProfileSpecs.byFilterParam;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;

    @Override
    public ProjectResp create(ProjectReq request) {
        log.info("create: {}", request);
        ProjectEntity entity = modelMapper.toProjectEntity(request);
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

    private void mergeRequestToEntity(ProjectReq request, ProjectEntity entity) {
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
    }

    @Override
    public List<ProjectResp> getAllByParam(ProjectFilterParam param) {
        log.info("get all by param: {}", param);
        List<ProjectEntity> entities = projectRepository.findAll(
                byFilterParam(param));

        return entities.stream()
                .map(modelMapper::toProjectResp)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, ProjectUpdateStatusReq request) {
        log.info("update status id: {} status: {}", id, request);
        ProjectEntity entity = getProjectEntity(id);
        if (request.getStatus().ordinal() < entity.getStatus().ordinal()) {
            log.warn("incorrect flow update status id: {} status: {}", id, request);
            throw new ProjectStatusIncorrectFlowUpdateException(
                    String.format("project status incorrect flow update: %s -> %s",
                            entity.getStatus().name(),
                            request.getStatus().name())
            );
        }
        entity.setStatus(request.getStatus());
    }

    @Override
    public ProjectEntity getProjectEntity(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("project not found " + id));
    }

    @Override
    @Transactional
    public void addEmployee(Integer projectId, EmployeeEntity employee, EmployeeRole role) {
        log.info("add employee {} {} in project {}", employee.getId(), role, projectId);
        ProjectEntity entity = getProjectEntity(projectId);

        // проверка что в команде нет такого сотрудника и нет такой роли
        Set<TeamEmbeddable> teams = entity.getTeams();
        for (TeamEmbeddable emb : teams) {
            if (emb.getRole().equals(role)) {
                log.warn("role {} already exists in team {}", role, projectId);
                throw new RoleAlreadyExistsInTeamException("role already exists - " + role.name());
            }
            if (emb.getEmployee().equals(employee)) {
                log.warn("employee {} already exists in team {}", employee.getId(), projectId);
                throw new EmployeeAlreadyExistsInTeamException("employee already exists - " + employee.getId());
            }
        }

        TeamEmbeddable teamEmbeddable = modelMapper.toTeamEmbeddable(employee, role);
        teams.add(teamEmbeddable);
    }

    @Override
    @Transactional
    public void removeEmployee(Integer projectId, Integer employeeId) {
        log.info("remove employee {} from project {}", employeeId, projectId);
        ProjectEntity entity = getProjectEntity(projectId);

        // находим сотрудика в проекте
        TeamEmbeddable teamEmbeddable = entity.getTeams().stream()
                .filter(team -> team.getEmployee().getId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("project team not found employee " + employeeId));

        entity.getTeams().remove(teamEmbeddable);
    }

    @Override
    @Transactional
    public List<TeamResp> getAllTeamEmployee(Integer projectId) {
        log.info("get team {}", projectId);
        ProjectEntity entity = getProjectEntity(projectId);
        return entity.getTeams().stream()
                .map(modelMapper::toTeamResp)
                .collect(Collectors.toList());
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
