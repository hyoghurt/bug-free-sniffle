package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.repository.TaskRepository;
import org.example.tracker.dto.task.*;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.TaskService;
import org.example.tracker.service.exception.EmployeeAlreadyDeletedException;
import org.example.tracker.service.exception.EmployeeNotFoundInTeamException;
import org.example.tracker.service.exception.TaskNotFoundException;
import org.example.tracker.service.exception.TaskStatusIncorrectFlowUpdateException;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.tracker.dao.repository.specification.TaskSpecs.byFilterParam;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    @Override
    @Transactional
    public TaskResp create(TaskReq request) {
        log.info("create: {}", request);
        TaskEntity entity = validate(null, request);
        taskRepository.save(entity);
        return modelMapper.toTaskResp(entity);
    }

    @Override
    @Transactional
    public TaskResp update(Integer id, TaskReq request) {
        log.info("update id: {} body: {}", id, request);
        TaskEntity entity = validate(id, request);
        entity.setUpdateDatetime(Instant.now());
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setLaborCostsInHours(request.getLaborCostsInHours());
        entity.setDeadlineDatetime(request.getDeadlineDatetime());
        return modelMapper.toTaskResp(entity);
    }

    @Override
    public List<TaskResp> getAllByParam(TaskFilterParam param) {
        log.info("get all by param: {}", param);
        List<TaskEntity> entities = taskRepository.findAll(
                byFilterParam(param));

        return entities.stream()
                .map(modelMapper::toTaskResp)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, TaskUpdateStatusReq request) {
        log.info("update status id: {} status: {}", id, request);
        TaskEntity entity = getTaskEntity(id);

        // check author in project team
        String upn = SecurityContextHolder.getContext().getAuthentication().getName();
        if (entity.getProject().getTeams().stream()
                .noneMatch(team -> team.getEmployee().getUpn().equals(upn))) {
            log.warn("author {} not found in team", upn);
            throw new EmployeeNotFoundInTeamException(String.format("employee{upn:%s} not found in team", upn));
        }

        TaskStatus newStatus = request.getStatus();
        TaskStatus currentStatus = entity.getStatus();
        if (newStatus.ordinal() < currentStatus.ordinal()) {
            log.warn("task status incorrect flow update {} -> {}",
                    currentStatus.name(), newStatus.name());
            throw new TaskStatusIncorrectFlowUpdateException(String
                    .format("task status incorrect flow update: %s -> %s",
                            currentStatus.name(),
                            newStatus.name()));
        }
        entity.setStatus(newStatus);
    }

    private TaskEntity validate(Integer taskId, TaskReq request) {
        TaskEntity entity = (taskId != null) ? getTaskEntity(taskId) : modelMapper.toTaskEntity(request);
        String upn = SecurityContextHolder.getContext().getAuthentication().getName();

        EmployeeEntity authorEntity = employeeService.getEmployeeEntityByUpn(upn);
        EmployeeEntity assigneesEntity = (request.getAssigneesId() != null) ?
                employeeService.getEmployeeEntity(request.getAssigneesId()) : null;
        ProjectEntity projectEntity = projectService.getProjectEntity(request.getProjectId());

        // проверка что сотрудники в команде
        validateEmployeeInTeam(projectEntity, authorEntity);
        validateEmployeeInTeam(projectEntity, assigneesEntity);

        // проверка что исполнитель не удален
        validateEmployeeNotDeleted(assigneesEntity);

        entity.setProject(projectEntity);
        entity.setAuthorId(authorEntity.getId());
        entity.setAssignees(assigneesEntity);

        return entity;
    }

    private void validateEmployeeInTeam(ProjectEntity projectEntity, EmployeeEntity employeeEntity) {
        if (employeeEntity == null) return;
        if (!projectService.isInTeam(projectEntity, employeeEntity.getId())) {
            log.warn("employee {} not found in team {}", employeeEntity.getId(), projectEntity.getId());
            throw new EmployeeNotFoundInTeamException("employee " + employeeEntity.getId() + " not found in team");
        }
    }

    private void validateEmployeeNotDeleted(EmployeeEntity employeeEntity) {
        if (employeeEntity == null) return;
        if (employeeService.isDeleted(employeeEntity)) {
            log.warn("employee {} is deleted", employeeEntity.getId());
            throw new EmployeeAlreadyDeletedException("employee already deleted: " + employeeEntity.getId());
        }
    }

    private TaskEntity getTaskEntity(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("task not found " + id));
    }
}
