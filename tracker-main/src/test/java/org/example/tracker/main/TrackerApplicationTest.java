package org.example.tracker.main;

import org.aspectj.lang.annotation.Before;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.repository.ProjectRepository;
import org.example.tracker.dto.project.ProjectFilterParam;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectStatus;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.mapper.ModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:sql/insert-projects.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "delete from projects")
class TrackerApplicationTest {

    @Autowired
    ProjectService service;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProjectRepository repository;

    ProjectEntity getEntity(String code, String name, String desc, String status) {
        ProjectEntity entity = new ProjectEntity();
        if (desc != null) entity.setDescription(desc);
        entity.setCode(code);
        entity.setName(name);
        entity.setDescription(desc);
        entity.setStatus(ProjectStatus.valueOf(status));
        return entity;
    }

    List<ProjectEntity> entities = new ArrayList<>();

    @BeforeEach
    void before() {
        System.out.println("CREATE PROJECT ENTITIES--------------------------");
        entities.add(getEntity("code20", "name", "desc", "DRAFT"));
        entities.add(getEntity("code21", "name", "desc", "DRAFT"));
        entities.add(getEntity("code22", "name", "desc", "IN_TESTING"));
        entities.add(getEntity("code_test", "name", "desc", "DRAFT"));
        entities.add(getEntity("test_code", "name", "desc", "IN_TESTING"));
        entities.add(getEntity("ctestc", "name", "desc", "FINISHED"));
        entities.add(getEntity("cteSTc", "name", "desc", "FINISHED"));
        entities.add(getEntity("test", "name", "desc", "IN_TESTING"));
        entities.add(getEntity("code1", "test", "desc", "IN_TESTING"));
        entities.add(getEntity("code2", "testname", "desc", "FINISHED"));
        entities.add(getEntity("code3", "nameTest", "desc", "IN_TESTING"));
        entities.add(getEntity("code43", "name", null, "DRAFT"));
        initCreate();
    }

    void initCreate() {
        entities.forEach(e -> {
            ProjectEntity save = repository.save(e);
            e.setId(save.getId());
        });
    }

    @Test
    void create() {
        ProjectReq request = ProjectReq.builder()
                .name("createTest")
                .code("createTest")
                .build();
        service.create(request);
        // TODO test check status
    }

    @Test
    void filterCodeName() {
        String search = "test";
        ProjectFilterParam param = ProjectFilterParam.builder()
                .query(search)
                .build();
        List<ProjectResp> actual = service.findByParam(param);
        assertEquals(entities.stream()
                .filter(e -> e.getName().toUpperCase().contains(search.toUpperCase())
                        || e.getCode().toUpperCase().contains(search.toUpperCase()))
                .map(modelMapper::toProjectResp)
                .collect(Collectors.toList()), actual);
    }

    @Test
    void filterNull() {
        ProjectFilterParam param = ProjectFilterParam.builder()
                .build();
        List<ProjectResp> actual = service.findByParam(param);
        assertEquals(entities.stream()
                .map(modelMapper::toProjectResp)
                .collect(Collectors.toList()), actual);
    }

    @Test
    void filterStatus() {
        List<ProjectStatus> statuses = List.of(ProjectStatus.DRAFT, ProjectStatus.FINISHED);
        ProjectFilterParam param = ProjectFilterParam.builder()
                .statuses(statuses)
                .build();
        List<ProjectResp> actual = service.findByParam(param);
        assertEquals(entities.stream()
                .filter(e -> statuses.stream().anyMatch(s -> e.getStatus().equals(s)))
                .map(modelMapper::toProjectResp)
                .collect(Collectors.toList()), actual);
    }

    @Test
    void filterCodeNameStatus() {
        String search = "test";
        List<ProjectStatus> statuses = List.of(ProjectStatus.DRAFT, ProjectStatus.FINISHED);
        ProjectFilterParam param = ProjectFilterParam.builder()
                .query(search)
                .statuses(statuses)
                .build();
        List<ProjectResp> actual = service.findByParam(param);
        assertEquals(entities.stream()
                .filter(e ->
                        (e.getName().toUpperCase().contains(search.toUpperCase())
                                || e.getCode().toUpperCase().contains(search.toUpperCase()))
                                && statuses.stream().anyMatch(s -> e.getStatus().equals(s))
                )
                .map(modelMapper::toProjectResp)
                .collect(Collectors.toList()), actual);
    }
}