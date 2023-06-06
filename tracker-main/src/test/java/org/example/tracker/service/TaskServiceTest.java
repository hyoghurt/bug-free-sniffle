package org.example.tracker.service;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.repository.EmployeeRepository;
import org.example.tracker.dao.repository.ProjectRepository;
import org.example.tracker.dao.repository.TaskRepository;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.project.ProjectStatus;
import org.example.tracker.dto.task.*;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.service.exception.EmployeeAlreadyDeletedException;
import org.example.tracker.service.exception.EmployeeNotFoundInTeamException;
import org.example.tracker.service.exception.TaskStatusIncorrectFlowUpdateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        statements = {"delete from tasks", "delete from teams", "delete from employees", "delete from projects"})
class TaskServiceTest extends BaseTest {

    @Autowired
    TeamService teamService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    ProjectService projectService;
    @Autowired
    TaskService taskService;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    ProjectRepository projectRepository;

    EmployeeResp employeeResp;
    ProjectResp projectResp;

    @BeforeEach
    void init() {
        // create employee
        EmployeeReq employeeReq = genEmployeeReq("upn", "first", "last");
        employeeResp = employeeService.create(employeeReq);

        // create project
        ProjectReq projectReq = genProjectReq("code", "name");
        projectResp = projectService.create(projectReq);

        // add employee in team
        TeamReq teamReq = genTeamReq(employeeResp.getId(), EmployeeRole.ANALYST);
        teamService.addEmployeeToProject(projectResp.getId(), teamReq);
    }

    EmployeeResp createRandomEmployee() {
        EmployeeReq employeeReq = genEmployeeReq(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString());
        return employeeService.create(employeeReq);
    }

    EmployeeEntity createRandomEmployeeEntity() {
        EmployeeEntity entity = EmployeeEntity.builder()
                .upn(UUID.randomUUID().toString())
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .status(EmployeeStatus.ACTIVE)
                .build();
        return employeeRepository.save(entity);
    }

    ProjectEntity createRandomProjectEntity() {
        ProjectEntity entity = ProjectEntity.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .status(ProjectStatus.IN_DEVELOPMENT)
                .build();
        return projectRepository.save(entity);
    }

    ProjectResp createRandomProject() {
        ProjectReq projectReq = genProjectReq(UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        return projectService.create(projectReq);
    }

    @Test
    void createTask() {
        TaskResp taskResp = createBase();
        assertEquals(taskResp.getStatus(), TaskStatus.OPEN);
        assertNotNull(taskResp.getCreatedDatetime());
    }

    @Test
    void createTask_assigneesNotInTeamException() {
        EmployeeResp employeeResp = createRandomEmployee();
        TaskReq taskReq = genTaskReq(projectResp.getId(), employeeResp.getId());
        assertThrows(EmployeeNotFoundInTeamException.class, () -> taskService.create(taskReq));
    }

    @Test
    void createTask_assigneesIsDeletedException() {
        employeeService.delete(employeeResp.getId());
        TaskReq taskReq = genTaskReq(projectResp.getId(), employeeResp.getId());
        assertThrows(EmployeeAlreadyDeletedException.class, () -> taskService.create(taskReq));
    }

    @Test
    void updateTask() {
        TaskResp taskResp = createBase();
        TaskReq taskReq = genTaskReq(projectResp.getId(), null);
        taskService.update(taskResp.getId(), taskReq);
        TaskEntity actual = taskRepository.findById(taskResp.getId()).orElseThrow(RuntimeException::new);
        assertEquals(taskReq.getTitle(), actual.getTitle());
    }

    @Test
    void updateTask_assigneesNotInTeamException() {
        EmployeeResp newEmployeeResp = createRandomEmployee();
        TaskResp taskResp = createBase();
        TaskReq taskReq = genTaskReq(projectResp.getId(), newEmployeeResp.getId());
        assertThrows(EmployeeNotFoundInTeamException.class, () -> taskService.update(taskResp.getId(), taskReq));
    }

    @Test
    void updateTask_assigneesIsDeletedException() {
        TaskResp taskResp = createBase();
        employeeService.delete(employeeResp.getId());
        TaskReq taskReq = genTaskReq(projectResp.getId(), employeeResp.getId());
        assertThrows(EmployeeAlreadyDeletedException.class, () -> taskService.update(taskResp.getId(), taskReq));
    }

    @Test
    void updateStatus() {
        TaskResp taskResp = updateStatusToDone();
        TaskEntity entity = taskRepository.findById(taskResp.getId()).orElseThrow(RuntimeException::new);
        assertEquals(entity.getStatus(), TaskStatus.DONE);
    }

    @Test
    void updateStatus_incorrectFlowException() {
        TaskResp taskResp = updateStatusToDone();
        TaskUpdateStatusReq request = TaskUpdateStatusReq.builder()
                .status(TaskStatus.OPEN)
                .build();

        assertThrows(TaskStatusIncorrectFlowUpdateException.class, () ->
                taskService.updateStatus(taskResp.getId(), request)
        );
    }

    @Test
    void filter() {
        EmployeeEntity emp1 = createRandomEmployeeEntity();
        EmployeeEntity emp2 = createRandomEmployeeEntity();

        ProjectEntity prj1 = createRandomProjectEntity();
        TaskEntity taskEntity = TaskEntity.builder()
                .title(UUID.randomUUID().toString())
                .deadlineDatetime(Instant.now())
                .laborCostsInHours(1)
                .authorId(emp2.getId())
                .assignees(emp1)
                .createdDatetime(Instant.now())
                .project(prj1)
                .status(TaskStatus.OPEN)
                .build();

        taskRepository.save(taskEntity);

        TaskFilterParam filter;
        List<TaskResp> active;

        filter = TaskFilterParam.builder()
                .assigneesId(emp2.getId())
                .build();
        active = taskService.findByParam(filter);
        assertEquals(0, active.size());

        filter = TaskFilterParam.builder()
                .assigneesId(emp1.getId())
                .build();
        active = taskService.findByParam(filter);
        assertEquals(1, active.size());
        assertTrue(active.stream()
                .anyMatch(t -> t.getAssigneesId().equals(emp1.getId())));
    }

    TaskResp createBase() {
        TaskReq request = genTaskReq(projectResp.getId(), null);
        return taskService.create(request);
    }

    TaskReq genTaskReq(Integer projectId, Integer assigneesId) {
        return TaskReq.builder()
                .title(UUID.randomUUID().toString())
                .projectId(projectId)
                .assigneesId(assigneesId)
                .laborCostsInHours(1)
                .deadlineDatetime(Instant.now().plus(2, ChronoUnit.HOURS))
                .build();
    }

    TaskResp updateStatusToDone() {
        TaskResp taskResp = createBase();
        TaskUpdateStatusReq request = TaskUpdateStatusReq.builder()
                .status(TaskStatus.DONE)
                .build();
        taskService.updateStatus(taskResp.getId(), request);
        return taskResp;
    }
}
