package org.example.tracker.main;

import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.repository.ProjectRepository;
import org.example.tracker.dto.project.*;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.exception.DuplicateUniqueFieldException;
import org.example.tracker.service.exception.ProjectStatusIncorrectFlowUpdateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:sql/insert-projects.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "delete from projects")
class ProjectServiceTest extends BaseTest {
    @Autowired
    ProjectService service;
    @Autowired
    ProjectRepository repository;

    List<ProjectEntity> entities = new ArrayList<>();

    @BeforeEach
    void before() {
        System.out.println("CREATE PROJECT ENTITIES--------------------------");
        entities.add(repository.save(genProjectEntity("code20", "name", "desc", "DRAFT")));
        entities.add(repository.save(genProjectEntity("code21", "name", "desc", "DRAFT")));
        entities.add(repository.save(genProjectEntity("code22", "name", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("code_test", "name", "desc", "DRAFT")));
        entities.add(repository.save(genProjectEntity("test_code", "name", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("ctestc", "name", "desc", "FINISHED")));
        entities.add(repository.save(genProjectEntity("cteSTc", "name", "desc", "FINISHED")));
        entities.add(repository.save(genProjectEntity("test", "name", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("code1", "test", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("code2", "testname", "desc", "FINISHED")));
        entities.add(repository.save(genProjectEntity("code3", "nameTest", "desc", "IN_TESTING")));
        entities.add(repository.save(genProjectEntity("code43", "name", null, "DRAFT")));
    }

    @Test
    void create() {
        ProjectResp resp = createRandomProject();
        assertEquals(resp.getStatus(), ProjectStatus.DRAFT);
    }

    @Test
    void create_duplicateException() {
        ProjectResp resp = createRandomProject();
        ProjectReq request = genProjectReq(resp.getCode(), "name");
        assertThrows(DuplicateUniqueFieldException.class, () -> service.create(request));
    }

    @Test
    void update() {
        ProjectResp resp = createRandomProject();

        ProjectReq update = genProjectReq(UUID.randomUUID().toString(), "nameUpdate");
        ProjectResp actual = service.update(resp.getId(), update);

        ProjectResp expected = ProjectResp.builder()
                .id(resp.getId())
                .code(update.getCode())
                .name(update.getName())
                .status(ProjectStatus.DRAFT)
                .build();

        assertEquals(actual, expected);
    }

    @Test
    void update_duplicateException() {
        ProjectResp resp = createRandomProject();
        ProjectResp resp2 = createRandomProject();
        ProjectReq update = genProjectReq(resp.getCode(), "nameUpdate");
        assertThrows(DuplicateUniqueFieldException.class, () -> service.update(resp2.getId(), update));
    }

    @Test
    void update_status() {
        ProjectResp resp = createRandomProject();
        assertEquals(resp.getStatus(), ProjectStatus.DRAFT);

        ProjectUpdateStatusReq statusReq = ProjectUpdateStatusReq.builder()
                .status(ProjectStatus.IN_DEVELOPMENT)
                .build();

        service.updateStatus(resp.getId(), statusReq);
        ProjectEntity entity = service.getProjectEntity(resp.getId());
        assertEquals(entity.getStatus(), ProjectStatus.IN_DEVELOPMENT);
    }

    @Test
    void update_status_exception() {
        ProjectEntity entity = genProjectEntity("code", "name", null, "IN_TESTING");
        ProjectEntity save = repository.save(entity);

        ProjectUpdateStatusReq statusReq = ProjectUpdateStatusReq.builder()
                .status(ProjectStatus.IN_DEVELOPMENT)
                .build();

        assertThrows(ProjectStatusIncorrectFlowUpdateException.class,
                () -> service.updateStatus(save.getId(), statusReq));
    }

    @Test
    void filterCodeName() {
        String search = "test";
        ProjectFilterParam param = ProjectFilterParam.builder()
                .query(search)
                .build();

        List<ProjectResp> actual = service.getAllByFilter(param);
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

        List<ProjectResp> actual = service.getAllByFilter(param);
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

        List<ProjectResp> actual = service.getAllByFilter(param);
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

        List<ProjectResp> actual = service.getAllByFilter(param);
        assertEquals(entities.stream()
                .filter(e ->
                        (e.getName().toUpperCase().contains(search.toUpperCase())
                                || e.getCode().toUpperCase().contains(search.toUpperCase()))
                                && statuses.stream().anyMatch(s -> e.getStatus().equals(s))
                )
                .map(modelMapper::toProjectResp)
                .collect(Collectors.toList()), actual);
    }

    ProjectResp createRandomProject() {
        ProjectReq request = genProjectReq(UUID.randomUUID().toString(), "name");
        return service.create(request);
    }
}