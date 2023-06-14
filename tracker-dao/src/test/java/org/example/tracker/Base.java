package org.example.tracker;

import org.example.tracker.config.JpaConfig;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.project.ProjectStatus;
import org.example.tracker.entity.EmployeeEntity;
import org.example.tracker.entity.ProjectEntity;
import org.example.tracker.entity.TaskEntity;
import org.example.tracker.repository.EmployeeRepository;
import org.example.tracker.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DataJpaTest
@ContextConfiguration(classes = JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class Base {

    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14.4");

    @DynamicPropertySource
    @SuppressWarnings("uncheck")
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        container.start();
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Autowired
    public EmployeeRepository employeeRepository;

    @Autowired
    public ProjectRepository projectRepository;

    public List<EmployeeEntity> employeeEntities;

    public List<ProjectEntity> projectEntities;


    public List<EmployeeEntity> initEmployeeEntities() {
        List<EmployeeEntity> entities = new ArrayList<>();
        System.out.println("CREATE Employee ENTITIES--------------------------");
        entities.add(employeeRepository.save(genEmployeeEntity("testName", "last", null, null, null, null, EmployeeStatus.ACTIVE)));
        entities.add(employeeRepository.save(genEmployeeEntity("name", "lastTest", null, null, null, null, EmployeeStatus.ACTIVE)));
        entities.add(employeeRepository.save(genEmployeeEntity("name", "last", "midtestname", null, null, null, EmployeeStatus.ACTIVE)));
        entities.add(employeeRepository.save(genEmployeeEntity("name", "last", null, "testmail", null, null, EmployeeStatus.ACTIVE)));
        entities.add(employeeRepository.save(genEmployeeEntity("name", "last", null, null, "testupn", null, EmployeeStatus.ACTIVE)));
        entities.add(employeeRepository.save(genEmployeeEntity("name", "last", null, null, null, "postest", EmployeeStatus.ACTIVE)));
        entities.add(employeeRepository.save(genEmployeeEntity("testFirst", "testLast", "testMiddle", "testEmail", "testUpn", "testPosition", EmployeeStatus.DELETED)));
        return entities;
    }

    public List<ProjectEntity> initProjectEntities() {
        List<ProjectEntity> entities = new ArrayList<>();
        System.out.println("CREATE Project ENTITIES--------------------------");
        entities.add(projectRepository.save(genProjectEntity("code20", "name", "desc", "DRAFT")));
        entities.add(projectRepository.save(genProjectEntity("code21", "name", "desc", "DRAFT")));
        entities.add(projectRepository.save(genProjectEntity("code22", "name", "desc", "IN_TESTING")));
        entities.add(projectRepository.save(genProjectEntity("code_test", "name", "desc", "DRAFT")));
        entities.add(projectRepository.save(genProjectEntity("test_code", "name", "desc", "IN_TESTING")));
        entities.add(projectRepository.save(genProjectEntity("cteSTc", "name", "desc", "FINISHED")));
        entities.add(projectRepository.save(genProjectEntity("test", "name", "desc", "IN_TESTING")));
        entities.add(projectRepository.save(genProjectEntity("code1", "test", "desc", "IN_TESTING")));
        entities.add(projectRepository.save(genProjectEntity("code2", "testname", "desc", "FINISHED")));
        entities.add(projectRepository.save(genProjectEntity("code3", "nameTest", "desc", "IN_TESTING")));
        entities.add(projectRepository.save(genProjectEntity("code43", "name", null, "DRAFT")));
        return entities;
    }

    public ProjectEntity genProjectEntity(String code, String name, String desc, String status) {
        return ProjectEntity.builder()
                .code(code)
                .name(name)
                .description(desc)
                .status(ProjectStatus.valueOf(status))
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
}