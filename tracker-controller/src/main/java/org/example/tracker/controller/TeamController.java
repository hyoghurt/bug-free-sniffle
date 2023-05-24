package org.example.tracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.service.TeamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping(value = "/v1/teams/{projectId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addEmployee(@PathVariable Integer projectId, @RequestBody @Valid TeamReq request) {
        teamService.addEmployeeToProject(projectId, request);
    }

    @DeleteMapping(value = "/v1/teams/{projectId}/{employeeId}")
    public void removeEmployee(@PathVariable Integer projectId, @PathVariable Integer employeeId) {
        teamService.removeEmployeeFromProject(projectId, employeeId);
    }

    @GetMapping(value = "/v1/teams/{projectId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TeamResp> getAllEmployee(@PathVariable Integer projectId) {
        return teamService.getProjectEmployees(projectId);
    }
}
