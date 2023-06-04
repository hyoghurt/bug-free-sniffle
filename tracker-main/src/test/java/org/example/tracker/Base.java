package org.example.tracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dao.repository.ProjectRepository;
import org.example.tracker.dao.repository.TaskRepository;
import org.example.tracker.helper.ModelGenerate;
import org.example.tracker.service.mapper.ModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Base extends ModelGenerate {

    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14.4");

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        container.start();
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

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

        taskRepository.deleteAll();
        projectRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    String asJsonString(final Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }
}