package org.example.tracker.service;

import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.project.ProjectReq;
import org.example.tracker.dto.project.ProjectResp;
import org.example.tracker.dto.team.EmployeeRole;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.service.exception.EmployeeAlreadyExistsInTeamException;
import org.example.tracker.service.exception.EmployeeNotFoundException;
import org.example.tracker.service.exception.RoleAlreadyExistsInTeamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        statements = {"delete from teams", "delete from employees", "delete from projects"})
class TeamServiceTest extends BaseTest {

    @Autowired
    TeamService teamService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    ProjectService projectService;

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
    }

    @Test
    void addTeamMember() {
        // check empty project team
        List<TeamResp> actual = teamService.getProjectEmployees(projectResp.getId());
        assertEquals(actual.size(), 0);

        // add employee in team
        TeamReq teamReq = genTeamReq(employeeResp.getId(), EmployeeRole.ANALYST);
        teamService.addEmployeeToProject(projectResp.getId(), teamReq);

        // check project team
        actual = teamService.getProjectEmployees(projectResp.getId());
        assertEquals(actual.size(), 1);
        assertEquals(actual.get(0).getEmployee().getId(), employeeResp.getId());
    }

    @Test
    void addTeamMember_roleAlreadyExistsException() {
        // add employee in team
        TeamReq teamReq = genTeamReq(employeeResp.getId(), EmployeeRole.ANALYST);
        teamService.addEmployeeToProject(projectResp.getId(), teamReq);

        // create new employee
        EmployeeReq employeeReq = genRandomEmployeeReq();
        EmployeeResp newEmployeeResp = employeeService.create(employeeReq);

        // add employee in team
        TeamReq teamReq1 = genTeamReq(newEmployeeResp.getId(), EmployeeRole.ANALYST);
        assertThrows(RoleAlreadyExistsInTeamException.class, () ->
                teamService.addEmployeeToProject(projectResp.getId(), teamReq1)
        );
    }

    @Test
    void addTeamMember_employeeAlreadyExistsException() {
        // add employee in team
        TeamReq teamReq = genTeamReq(employeeResp.getId(), EmployeeRole.ANALYST);
        teamService.addEmployeeToProject(projectResp.getId(), teamReq);

        // add employee in team
        TeamReq teamReq1 = genTeamReq(employeeResp.getId(), EmployeeRole.PROJECT_MANAGER);
        assertThrows(EmployeeAlreadyExistsInTeamException.class, () ->
                teamService.addEmployeeToProject(projectResp.getId(), teamReq1)
        );
    }

    @Test
    void removeTeamMember() {
        // check empty project team
        List<TeamResp> actual = teamService.getProjectEmployees(projectResp.getId());
        assertEquals(actual.size(), 0);

        // add employee in team
        TeamReq teamReq = genTeamReq(employeeResp.getId(), EmployeeRole.ANALYST);
        teamService.addEmployeeToProject(projectResp.getId(), teamReq);

        // check project team not empty
        actual = teamService.getProjectEmployees(projectResp.getId());
        assertEquals(actual.size(), 1);

        // remove employee from project
        teamService.removeEmployeeFromProject(projectResp.getId(), employeeResp.getId());

        // check empty project team
        actual = teamService.getProjectEmployees(projectResp.getId());
        assertEquals(actual.size(), 0);
    }

    @Test
    void removeTeamMember_notFoundException() {
        assertThrows(EmployeeNotFoundException.class, () ->
                teamService.removeEmployeeFromProject(projectResp.getId(), employeeResp.getId())
        );
    }
}
