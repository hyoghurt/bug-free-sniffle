package org.example.tracker.main;

import org.example.tracker.dao.entity.TaskEntity;
import org.example.tracker.dao.repository.TaskRepository;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.task.TaskReq;
import org.example.tracker.dto.task.TaskResp;
import org.example.tracker.dto.task.TaskStatus;
import org.example.tracker.dto.task.TaskUpdateStatusReq;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.service.EmployeeService;
import org.example.tracker.service.ProjectService;
import org.example.tracker.service.TaskService;
import org.example.tracker.service.TeamService;
import org.example.tracker.service.exception.EmployeeAlreadyDeletedException;
import org.example.tracker.service.exception.EmployeeNotFoundInTeamException;
import org.example.tracker.service.exception.TaskStatusIncorrectFlowUpdateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        TeamReq teamReq = genTeamReq(projectResp.getId(), employeeResp.getId(), EmployeeRole.ANALYST);
        teamService.addEmployeeToProject(teamReq);
    }

    EmployeeResp createRandomEmployee() {
        EmployeeReq employeeReq = genEmployeeReq(UUID.randomUUID().toString(), "first", "last");
        return employeeService.create(employeeReq);
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