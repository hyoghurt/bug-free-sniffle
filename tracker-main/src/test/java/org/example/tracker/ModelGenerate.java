package org.example.tracker;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectStatus;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamReq;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class ModelGenerate {

    // TASK
    public TaskEntity genRandomTaskEntity(Integer authorId, EmployeeEntity assignees, ProjectEntity project) {
        return TaskEntity.builder()
                .authorId(authorId)
                .assignees(assignees)
                .project(project)
                .title(UUID.randomUUID().toString())
                .createdDatetime(Instant.now())
                .laborCostsInHours(1)
                .deadlineDatetime(Instant.now().plus(4, ChronoUnit.HOURS))
                .build();
    }

    public TaskEntity genRandomTaskEntity(Integer authorId, EmployeeEntity assignees, ProjectEntity project, Instant created) {
        return TaskEntity.builder()
                .authorId(authorId)
                .assignees(assignees)
                .project(project)
                .title(UUID.randomUUID().toString())
                .createdDatetime(created)
                .laborCostsInHours(1)
                .deadlineDatetime(created.plus(5, ChronoUnit.HOURS))
                .build();
    }


    public TaskReq genRandomTaskReq(Integer projectId, Integer assigneesId) {
        return TaskReq.builder()
                .title(UUID.randomUUID().toString())
                .projectId(projectId)
                .assigneesId(assigneesId)
                .laborCostsInHours(1)
                .deadlineDatetime(Instant.now().plus(6, ChronoUnit.HOURS))
                .build();
    }


    // TEAM
    public TeamReq genTeamReq(Integer employeeId, EmployeeRole role) {
        return TeamReq.builder()
                .employeeId(employeeId)
                .role(role)
                .build();
    }


    // PROJECT
    public ProjectReq genProjectReq(String code, String name) {
        return ProjectReq.builder()
                .code(code)
                .name(name)
                .build();
    }

    public ProjectEntity genProjectEntity(String code, String name, String desc, String status) {
        return ProjectEntity.builder()
                .code(code)
                .name(name)
                .description(desc)
                .status(ProjectStatus.valueOf(status))
                .build();
    }

    public ProjectReq genRandomProjectReq() {
        return ProjectReq.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .build();
    }

    public ProjectEntity genRandomProjectEntity() {
        return ProjectEntity.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .build();
    }

    public ProjectEntity genRandomProjectEntity(ProjectStatus status) {
        return ProjectEntity.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .status(status)
                .build();
    }


    // EMPLOYEE
    public EmployeeReq genEmployeeReq(String upn, String first, String last) {
        return EmployeeReq.builder()
                .upn(upn)
                .firstName(first)
                .lastName(last)
                .build();
    }

    public EmployeeEntity genEmployeeEntity(String firstName, String lastName, String middleName,
                                     String email, String upn, String position, EmployeeStatus status) {
        return EmployeeEntity.builder()
                .upn(upn)
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .position(position)
                .email(email)
                .status(status)
                .build();
    }

    public EmployeeEntity genRandomEmployeeEntity() {
        return EmployeeEntity.builder()
                .upn(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .build();
    }

    public EmployeeEntity genRandomEmployeeEntity(EmployeeStatus status) {
        EmployeeEntity entity = genRandomEmployeeEntity();
        entity.setStatus(status);
        return entity;
    }

    public EmployeeReq genRandomEmployeeReq() {
        return EmployeeReq.builder()
                .upn(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .build();
    }
}