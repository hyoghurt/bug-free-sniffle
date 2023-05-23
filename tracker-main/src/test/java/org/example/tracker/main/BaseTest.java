package org.example.tracker.main;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectStatus;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.service.mapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class BaseTest {
    @Autowired
    ModelMapper modelMapper;

    EmployeeReq genEmployeeReq(String upn, String first, String last) {
        return EmployeeReq.builder()
                .upn(upn)
                .firstName(first)
                .lastName(last)
                .build();
    }

    EmployeeEntity genEmployeeEntity(String firstName, String lastName, String middleName,
                                     String email, String upn, String position, EmployeeStatus status) {
        EmployeeEntity entity = new EmployeeEntity();
        if (position != null) entity.setPosition(position);
        if (email != null) entity.setEmail(email);
        if (firstName != null) entity.setFirstName(firstName);
        if (lastName != null) entity.setLastName(lastName);
        if (middleName != null) entity.setMiddleName(middleName);
        if (upn != null) entity.setUpn(upn);
        if (status != null) entity.setStatus(status);
        return entity;
    }

    ProjectEntity genProjectEntity(String code, String name, String desc, String status) {
        ProjectEntity entity = new ProjectEntity();
        if (desc != null) entity.setDescription(desc);
        entity.setCode(code);
        entity.setName(name);
        entity.setDescription(desc);
        entity.setStatus(ProjectStatus.valueOf(status));
        return entity;
    }

    ProjectReq genProjectReq(String code, String name) {
        return ProjectReq.builder()
                .code(code)
                .name(name)
                .build();
    }

    TeamReq genTeamReq(Integer projectId, Integer employeeId, EmployeeRole role) {
        return TeamReq.builder()
                .projectId(projectId)
                .employeeId(employeeId)
                .role(role)
                .build();
    }
}