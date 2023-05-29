package org.example.tracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dao.repository.ProjectRepository;
import org.example.tracker.dao.repository.TaskRepository;
import org.example.tracker.ModelGenerate;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.service.mapper.ModelMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationTest extends ModelGenerate {

    MockMvc mvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    EmployeeRepository employeeRepository;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void resetDB() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    String asJsonString(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

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
}