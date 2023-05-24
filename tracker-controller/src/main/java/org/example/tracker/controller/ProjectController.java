package org.example.tracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tracker.dto.project.*;
import org.example.tracker.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/v1/projects",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectResp create(@RequestBody @Valid ProjectReq request) {
        return projectService.create(request);
    }

    @PutMapping(value = "/v1/projects/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectResp update(@PathVariable Integer id, @RequestBody @Valid ProjectReq request) {
        return projectService.update(id, request);
    }

    @GetMapping (value = "/v1/projects",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectResp> getAllByFilter(@RequestParam(required = false) String query,
                                            @RequestParam(required = false) List<ProjectStatus> statuses) {
        return projectService.getAllByFilter(new ProjectFilterParam(query, statuses));
    }

    @PutMapping(value = "/v1/projects/{id}/status",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatus(@PathVariable Integer id, @RequestBody @Valid ProjectUpdateStatusReq request) {
        projectService.updateStatus(id, request);
    }
}
