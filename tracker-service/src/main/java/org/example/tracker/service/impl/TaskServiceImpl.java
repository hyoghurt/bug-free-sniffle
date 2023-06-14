package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.repository.TaskRepository;
import org.example.tracker.dto.task.*;
import org.example.tracker.service.EmailService;
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
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.example.tracker.dao.specification.TaskSpecs.byFilterParam;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;
    private final ProjectService projectService;
    private final EmailService emailService;

    @Override
    public TaskResp create(TaskReq request) {
        log.info("create: {}", request);
        TaskEntity entity = createOrUpdate(null, request);
        return modelMapper.toTaskResp(entity);
    }

    @Override
    public TaskResp update(Integer id, TaskReq request) {
        log.info("update id: {} body: {}", id, request);
        TaskEntity entity = createOrUpdate(id, request);
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
        validateEmployeeInTeam(entity.getProject(), upn);

        TaskStatus newStatus = request.getStatus();
        TaskStatus currentStatus = entity.getStatus();
        if (newStatus.ordinal() < currentStatus.ordinal()) {
            log.warn("task status incorrect flow update {} -> {}", currentStatus.name(), newStatus.name());
            throw new TaskStatusIncorrectFlowUpdateException(String
                    .format("task status incorrect flow update: %s -> %s",
                            currentStatus.name(),
                            newStatus.name()));
        }
        entity.setStatus(newStatus);
    }

    @Transactional
    private TaskEntity createOrUpdate(Integer taskId, TaskReq request) {
        TaskEntity taskEntity;

        if (taskId != null) {
            taskEntity = getTaskEntity(taskId);
            taskEntity.setUpdateDatetime(Instant.now());
            taskEntity.setTitle(request.getTitle());
            taskEntity.setDescription(request.getDescription());
            taskEntity.setLaborCostsInHours(request.getLaborCostsInHours());
            taskEntity.setDeadlineDatetime(request.getDeadlineDatetime());
        } else {
            taskEntity = modelMapper.toTaskEntity(request);
        }

        String upn = SecurityContextHolder.getContext().getAuthentication().getName();

        EmployeeEntity assigneesEntity = taskEntity.getAssignees();
        EmployeeEntity authorEntity = employeeService.getEmployeeEntityByUpn(upn);
        ProjectEntity projectEntity = projectService.getProjectEntity(request.getProjectId());

        boolean isNewAssignees = isNewAssignees(request.getAssigneesId(), assigneesEntity);

        if (isNewAssignees) {
            assigneesEntity = employeeService.getEmployeeEntity(request.getAssigneesId());
            // проверка что исполнитель не удален
            validateEmployeeNotDeleted(assigneesEntity);
            // проверка что исполнитель в команде
            validateEmployeeInTeam(projectEntity, assigneesEntity);
            taskEntity.setAssignees(assigneesEntity);

        } else if (request.getAssigneesId() == null) {
            taskEntity.setAssignees(null);
        }

        // проверка что автор в команде
        validateEmployeeInTeam(projectEntity, authorEntity);
        taskEntity.setAuthorId(authorEntity.getId());

        taskEntity.setProject(projectEntity);
        taskRepository.save(taskEntity);

        // отправка исполнителю уведомления
        if (isNewAssignees) {
            Executors.newSingleThreadExecutor()
                    .submit(() -> sendEmail(taskEntity));
        }

        return taskEntity;
    }

    private boolean isNewAssignees(Integer newId, EmployeeEntity entity) {
        if (newId == null) {
            return false;
        } else if (entity == null) {
            return true;
        }
        return !newId.equals(entity.getId());
    }

    private void validateEmployeeInTeam(ProjectEntity projectEntity, String upn) {
        if (!projectService.isInTeam(projectEntity, upn)) {
            log.warn("employee {} not found in team {}", upn, projectEntity.getId());
            throw new EmployeeNotFoundInTeamException(String.format("employee{upn:%s} not found in team %d",
                    upn, projectEntity.getId()));
        }
    }

    private void validateEmployeeInTeam(ProjectEntity projectEntity, EmployeeEntity employeeEntity) {
        if (employeeEntity == null) return;
        if (!projectService.isInTeam(projectEntity, employeeEntity.getId())) {
            log.warn("employee {} not found in team {}", employeeEntity.getId(), projectEntity.getId());
            throw new EmployeeNotFoundInTeamException(String.format("employee{id:%d} not found in team %d",
                    employeeEntity.getId(), projectEntity.getId()));
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

    private boolean sendEmail(TaskEntity entity) {
        EmployeeEntity assignees = entity.getAssignees();
        if (assignees != null && assignees.getEmail() != null) {
            String subject = "Новая задача";
            String text = String.format("%s, у тебя новая задача %d. %s",
                    assignees.getFirstName(), entity.getId(), entity.getTitle());

            EmailService.EmailDetails details = EmailService.EmailDetails.builder()
                    .to(assignees.getEmail())
                    .subject(subject)
                    .text(text)
                    .build();

            return emailService.send(details);
        }
        return false;
    }
}
