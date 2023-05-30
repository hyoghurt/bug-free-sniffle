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
import org.example.tracker.dto.project.*;
import org.example.tracker.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "project", description = "управление проектами")
@SecurityRequirement(name = "basicScheme")
@ApiResponse(responseCode = "401", content = @Content)
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "создание",
            description = "Создать проект. Проект создается в статусе DRAFT.",
            responses = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/v1/project",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectResp create(@RequestBody @Valid ProjectReq request) {

        return projectService.create(request);
    }

    @Operation(summary = "изменение",
            description = "Изменить проект.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @PutMapping(value = "/v1/project/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectResp update(@Parameter(description = "уникальный идентификатор проекта")
                              @PathVariable Integer id,
                              @RequestBody @Valid ProjectReq request) {

        return projectService.update(id, request);
    }

    @Operation(summary = "поиск",
            description = "Поиск проектов. Поиск осуществляеться по текстовому значению " +
                    "(по полям Наименование и Код проекта) и с применением фильтров по Статусу проекта. " +
                    "Если параметры не переданы, то вернуться все проекты.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @GetMapping(value = "/v1/projects",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectResp> getAllByFilter(@Parameter(description = "текстовое значение для поиска")
                                            @RequestParam(required = false) String query,
                                            @Parameter(description = "список статусов для фильтра")
                                            @RequestParam(required = false) List<ProjectStatus> statuses) {

        return projectService.getAllByParam(new ProjectFilterParam(query, statuses));
    }

    @Operation(summary = "изменение статуса",
            description = "Перевести проект в другой статус. Можно перевести в другой статус проект, " +
                    "согласно последовательности изменения статуса.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @PutMapping(value = "/v1/project/{id}/status",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatus(@Parameter(description = "уникальный идентификатор проекта")
                             @PathVariable Integer id,
                             @RequestBody @Valid ProjectUpdateStatusReq request) {

        projectService.updateStatus(id, request);
    }
}
