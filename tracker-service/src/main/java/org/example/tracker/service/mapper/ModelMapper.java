package org.example.tracker.service.mapper;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.entity.TeamEmbeddable;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamResp;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {
    public EmployeeEntity toEmployeeEntity(EmployeeReq dto) {
        return EmployeeEntity.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .position(dto.getPosition())
                .email(dto.getEmail())
                .upn(dto.getUpn())
                .build();
    }

    public EmployeeResp toEmployeeResp(EmployeeEntity entity) {
        return EmployeeResp.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .position(entity.getPosition())
                .email(entity.getEmail())
                .upn(entity.getUpn())
                .build();
    }

    public ProjectEntity toProjectEntity(ProjectReq dto) {
        return ProjectEntity.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public TaskEntity toTaskEntity(TaskReq dto) {
        return TaskEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .laborCostsInHours(dto.getLaborCostsInHours())
                .deadlineDatetime(dto.getDeadlineDatetime())
                .build();
    }

    public TeamEmbeddable toTeamEmbeddable(EmployeeEntity entity, EmployeeRole role) {
        return TeamEmbeddable.builder()
                .role(role)
                .employee(entity)
                .build();
    }

    public ProjectResp toProjectResp(ProjectEntity entity) {
        return ProjectResp.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .build();
    }

    public TaskResp toTaskResp(TaskEntity entity) {
        TaskResp dto = TaskResp.builder()
                .id(entity.getId())
                .projectId(entity.getProject().getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .authorId(entity.getAuthorId())
                .laborCostsInHours(entity.getLaborCostsInHours())
                .createdDatetime(entity.getCreatedDatetime())
                .updateDatetime(entity.getUpdateDatetime())
                .deadlineDatetime(entity.getDeadlineDatetime())
                .build();
        if (entity.getAssignees() != null) dto.setAssigneesId(entity.getAssignees().getId());
        return dto;
    }

    public TeamResp toTeamResp(TeamEmbeddable entity) {
        return TeamResp.builder()
                .employee(toEmployeeResp(entity.getEmployee()))
                .role(entity.getRole())
                .build();
    }
}
