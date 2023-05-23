package org.example.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.repository.TaskRepository;
import org.example.tracker.dto.task.TaskFilterParam;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskUpdateStatusReq;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.TaskService;
import org.example.tracker.service.exception.EmployeeAlreadyDeletedException;
import org.example.tracker.service.exception.EmployeeNotFoundInTeamException;
import org.example.tracker.service.exception.TaskNotFoundException;
import org.example.tracker.service.exception.TaskStatusIncorrectFlowUpdateException;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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
        //TODO controller validate createdDatetime + трудозатраты <= deadline
        TaskEntity entity = modelMapper.toTaskEntity(request);

        initAuthor(entity, 1, request.getProjectId());
        initAssignees(entity, request.getAssigneesId(), request.getProjectId());

        taskRepository.save(entity);
        return modelMapper.toTaskResp(entity);
    }

    @Override
    @Transactional
    public TaskResp update(Integer id, TaskReq request) {
        TaskEntity entity = getTaskEntity(id);

        initAuthor(entity, 1, request.getProjectId());
        initAssignees(entity, request.getAssigneesId(), request.getProjectId());
        entity.setUpdateDatetime(Instant.now());

        taskRepository.save(entity);
        return modelMapper.toTaskResp(entity);
    }

    @Override
    public List<TaskResp> findByParam(TaskFilterParam param) {
        return taskRepository.findByFilter(param).stream()
                .map(modelMapper::toTaskResp)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, TaskUpdateStatusReq request) {
        TaskEntity entity = getTaskEntity(id);
        if (request.getStatus().ordinal() < entity.getStatus().ordinal()) {
            throw new TaskStatusIncorrectFlowUpdateException(String
                    .format("task status incorrect flow update: %s -> %s",
                            entity.getStatus().name(),
                            request.getStatus().name()));
        }
        entity.setStatus(request.getStatus());
    }

    void initAuthor(TaskEntity taskEntity, Integer authorId, Integer projectId) {
        // TODO get author id

        // проверка что автор в команде проекта
        // TODO check author in project team

        taskEntity.setAuthorId(authorId);
    }

    void initAssignees(TaskEntity taskEntity, Integer assigneesId, Integer projectId) {
        EmployeeEntity assigneesEntity = (assigneesId != null) ?
                employeeService.getEmployeeEntity(assigneesId) : null;

        // проверка что исполнитель не удален
        validateEmployeeNotDeleted(assigneesEntity);

        // проверка что исполнитель в команде проекта
        ProjectEntity projectEntity = projectService.getProjectEntity(projectId);
        validateEmployeeInTeam(projectEntity, assigneesEntity);

        taskEntity.setAssignees(assigneesEntity);
        taskEntity.setProject(projectEntity);
    }

    private void validateEmployeeInTeam(ProjectEntity projectEntity, EmployeeEntity employeeEntity) {
        if (employeeEntity == null) return;
        if (!projectService.isInTeam(projectEntity, employeeEntity.getId())) {
            throw new EmployeeNotFoundInTeamException("employee " + employeeEntity.getId() + " not found in team");
        }
    }

    private void validateEmployeeNotDeleted(EmployeeEntity employeeEntity) {
        if (employeeEntity == null) return;
        if (employeeService.isDeleted(employeeEntity)) {
            throw new EmployeeAlreadyDeletedException("employee already deleted: " + employeeEntity.getId());
        }
    }

    private TaskEntity getTaskEntity(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("task not found " + id));
    }
}
