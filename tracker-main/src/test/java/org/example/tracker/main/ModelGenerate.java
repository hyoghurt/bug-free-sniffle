package org.example.tracker.main;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectStatus;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamReq;

import java.util.UUID;

class ModelGenerate {

    TeamReq genTeamReq(Integer employeeId, EmployeeRole role) {
        return TeamReq.builder()
                .employeeId(employeeId)
                .role(role)
                .build();
    }


    // PROJECT
    ProjectReq genProjectReq(String code, String name) {
        return ProjectReq.builder()
                .code(code)
                .name(name)
                .build();
    }

    ProjectEntity genProjectEntity(String code, String name, String desc, String status) {
        return ProjectEntity.builder()
                .code(code)
                .name(name)
                .description(desc)
                .status(ProjectStatus.valueOf(status))
                .build();
    }

    ProjectReq genRandomProjectReq() {
        return ProjectReq.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .build();
    }

    ProjectEntity genRandomProjectEntity() {
        return ProjectEntity.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .build();
    }

    ProjectEntity genRandomProjectEntity(ProjectStatus status) {
        return ProjectEntity.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .status(status)
                .build();
    }


    // EMPLOYEE
    EmployeeReq genEmployeeReq(String upn, String first, String last) {
        return EmployeeReq.builder()
                .upn(upn)
                .firstName(first)
                .lastName(last)
                .build();
    }

    EmployeeEntity genEmployeeEntity(String firstName, String lastName, String middleName,
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

    EmployeeEntity genRandomEmployeeEntity() {
        return EmployeeEntity.builder()
                .upn(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .build();
    }

    EmployeeEntity genRandomEmployeeEntity(EmployeeStatus status) {
        EmployeeEntity entity = genRandomEmployeeEntity();
        entity.setStatus(status);
        return entity;
    }

    EmployeeReq genRandomEmployeeReq() {
        return EmployeeReq.builder()
                .upn(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .build();
    }
}