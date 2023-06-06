package org.example.tracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tracker.dto.error.ErrorResp;
import org.example.tracker.dto.team.TeamReq;
import org.example.tracker.dto.team.TeamResp;
import org.example.tracker.service.TeamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "team", description = "управление командами")
@SecurityRequirement(name = "basicScheme")
@ApiResponse(responseCode = "401", content = @Content)
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;

    @Operation(summary = "добавление",
            description = "Добавить сотрудника в команду проекта. " +
                    "В разных командах один сотрудник может принимать различные роли, " +
                    "но только одну роль внутри проекта.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @PostMapping(value = "/{projectId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addEmployee(@Parameter(description = "уникальный идентификатор проекта")
                            @PathVariable Integer projectId,
                            @RequestBody @Valid TeamReq request) {

        teamService.addEmployeeToProject(projectId, request);
    }

    //TODO test incorrect type /team/sdfdf/123
    @Operation(summary = "удаление",
            description = "Удалить сотрудника из команды проекта.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @DeleteMapping(value = "/{projectId}/employees/{employeeId}")
    public void removeEmployee(@Parameter(description = "уникальный идентификатор проекта")
                               @PathVariable Integer projectId,
                               @Parameter(description = "уникальный идентификатор сотрудника")
                               @PathVariable Integer employeeId) {

        teamService.removeEmployeeFromProject(projectId, employeeId);
    }

    //TODO test incorrect type /teams/sdfdf/123
    @Operation(summary = "получение",
            description = "Получить всех сотрудников проекта.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @GetMapping(value = "/{projectId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TeamResp> getAllEmployee(@Parameter(description = "уникальный идентификатор проекта")
                                         @PathVariable Integer projectId) {

        return teamService.getProjectEmployees(projectId);
    }
}
