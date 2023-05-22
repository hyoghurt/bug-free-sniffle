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
import org.example.tracker.dto.team.EmployeeRole;
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
                .projectId(dto.getProjectId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .assigneesId(dto.getAssigneesId())
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
        ProjectResp dto = new ProjectResp();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        return dto;


/*
        return ProjectResp.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .build();
*/
    }
}
