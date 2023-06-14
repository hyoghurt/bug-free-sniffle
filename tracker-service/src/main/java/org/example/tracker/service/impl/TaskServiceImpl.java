package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.amqp.message.NewTaskMsg;
import org.example.tracker.amqp.producer.NewTaskProducer;
import org.example.tracker.dto.task.*;
import org.example.tracker.entity.EmployeeEntity;
import org.example.tracker.entity.ProjectEntity;
import org.example.tracker.entity.TaskEntity;
import org.example.tracker.exception.EmployeeAlreadyDeletedException;
import org.example.tracker.exception.EmployeeNotFoundInTeamException;
import org.example.tracker.exception.TaskNotFoundException;
import org.example.tracker.exception.TaskStatusIncorrectFlowUpdateException;
import org.example.tracker.mapper.ModelMapper;
import org.example.tracker.repository.TaskRepository;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.TaskService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.tracker.repository.specification.TaskSpecs.byFilterParam;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;
    private final ProjectService projectService;
    private final NewTaskProducer newTaskProducer;

    @Override
    @Transactional
    public TaskResp create(TaskReq request) {
        log.info("create: {}", request);
        TaskEntity entity = new TaskEntity();
        mergeRequestToEntity(request, entity);
        checkAndSave(request, entity);
        return modelMapper.toTaskResp(entity);
    }

    @Override
    @Transactional
    public TaskResp update(Integer id, TaskReq request) {
        log.info("update id: {} body: {}", id, request);
        TaskEntity entity = getTaskEntity(id);
        entity.setUpdateDatetime(Instant.now());
        mergeRequestToEntity(request, entity);
        checkAndSave(request, entity);
        return modelMapper.toTaskResp(entity);
    }

    private void mergeRequestToEntity(TaskReq request, TaskEntity entity) {
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setLaborCostsInHours(request.getLaborCostsInHours());
        entity.setDeadlineDatetime(request.getDeadlineDatetime());
    }

    private void checkAndSave(TaskReq request, TaskEntity taskEntity) {
        boolean isNewAssignees = isNewAssignees(taskEntity.getAssignees(), request.getAssigneesId());

        ProjectEntity projectEntity = projectService.getProjectEntity(request.getProjectId());
        taskEntity.setProject(projectEntity);

        EmployeeEntity authorEntity = checkAndGetAuthor(projectEntity);
        taskEntity.setAuthorId(authorEntity.getId());

        if (request.getAssigneesId() == null) {
            taskEntity.setAssignees(null);
        } else if (isNewAssignees) {
            EmployeeEntity assigneesEntity = checkAndGetAssignees(projectEntity, request.getAssigneesId());
            taskEntity.setAssignees(assigneesEntity);
        }

        taskRepository.save(taskEntity);

        if (isNewAssignees) {
            newTaskProducer.send(new NewTaskMsg(taskEntity.getId()));
        }
    }

    private boolean isNewAssignees(EmployeeEntity entity, Integer newAssigneesId) {
        if (newAssigneesId == null) {
            return false;
        } else if (entity == null) {
            return true;
        }
        return !newAssigneesId.equals(entity.getId());
    }

    private EmployeeEntity checkAndGetAuthor(ProjectEntity projectEntity) {
        String upn = SecurityContextHolder.getContext().getAuthentication().getName();
        EmployeeEntity authorEntity = employeeService.getEmployeeEntityByUpn(upn);
        checkEmployeeInTeam(projectEntity, authorEntity);
        return authorEntity;
    }

    private EmployeeEntity checkAndGetAssignees(ProjectEntity projectEntity, Integer newAssigneesId) {
        EmployeeEntity assigneesEntity = employeeService.getEmployeeEntity(newAssigneesId);
        checkEmployeeIsNotDeleted(assigneesEntity);
        checkEmployeeInTeam(projectEntity, assigneesEntity);
        return assigneesEntity;
    }

    private void checkEmployeeInTeam(ProjectEntity projectEntity, EmployeeEntity employeeEntity) {
        if (employeeEntity == null) return;
        if (!projectService.isInTeam(projectEntity, employeeEntity.getId())) {
            log.info("employee {} not found in team {}", employeeEntity.getId(), projectEntity.getId());
            throw new EmployeeNotFoundInTeamException(String.format("employee{id:%d} not found in team %d",
                    employeeEntity.getId(), projectEntity.getId()));
        }
    }

    private void checkEmployeeIsNotDeleted(EmployeeEntity employeeEntity) {
        if (employeeEntity == null) return;
        if (employeeService.isDeleted(employeeEntity)) {
            log.info("employee {} is deleted", employeeEntity.getId());
            throw new EmployeeAlreadyDeletedException("employee already deleted: " + employeeEntity.getId());
        }
    }

    @Override
    public List<TaskResp> getAllByParam(TaskFilterParam param) {
        log.info("get all by param: {}", param);
        return taskRepository.findAll(byFilterParam(param))
                .stream()
                .map(modelMapper::toTaskResp)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, TaskUpdateStatusReq request) {
        log.info("update status id: {} status: {}", id, request);
        TaskEntity entity = getTaskEntity(id);
        checkAuthorInTeam(entity.getProject());
        checkTaskStatusFlow(entity.getStatus(), request.getStatus());
        entity.setStatus(request.getStatus());
    }

    @Override
    public TaskEntity getTaskEntity(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("task not found " + id));
    }

    private void checkAuthorInTeam(ProjectEntity entity) {
        String upn = SecurityContextHolder.getContext().getAuthentication().getName();
        checkEmployeeInTeam(entity, upn);
    }

    private void checkTaskStatusFlow(TaskStatus currentStatus, TaskStatus newStatus) {
        if (newStatus.ordinal() < currentStatus.ordinal()) {
            log.info("task status incorrect flow update {} -> {}", currentStatus.name(), newStatus.name());
            throw new TaskStatusIncorrectFlowUpdateException(String
                    .format("task status incorrect flow update: %s -> %s",
                            currentStatus.name(),
                            newStatus.name()));
        }
    }

    private void checkEmployeeInTeam(ProjectEntity projectEntity, String upn) {
        if (!projectService.isInTeam(projectEntity, upn)) {
            log.info("employee {} not found in team {}", upn, projectEntity.getId());
            throw new EmployeeNotFoundInTeamException(String.format("employee{upn:%s} not found in team %d",
                    upn, projectEntity.getId()));
        }
    }
}
