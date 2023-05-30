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
import org.example.tracker.dto.task.*;
import org.example.tracker.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "task", description = "управление задачами")
@SecurityRequirement(name = "basicScheme")
@ApiResponse(responseCode = "401", content = @Content)
//@Validated // validate path, param
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "создание",
            description = "Создание задачи. Задача автоматически создается в статусе OPEN. " +
                    "Автором задачи становиться тот, кто создал задачу.",
            responses = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/v1/task",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskResp create(@RequestBody @Valid TaskReq request) {

        return taskService.create(request);
    }

    @Operation(summary = "изменение",
            description = "Изменение задачи. Автор задачи будет изменен, на того, кто изменил задачу.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @PutMapping(value = "/v1/task/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskResp update(@Parameter(description = "уникальный идентификатор задачи")
                           @PathVariable Integer id,
                           @RequestBody @Valid TaskReq request) {

        return taskService.update(id, request);
    }

    @Operation(summary = "изменение статуса",
            description = "Изменение статуса задачи. " +
                    "Переход по статусам возможен согласно последовательности.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @PutMapping(value = "/v1/task/{id}/status",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatus(@Parameter(description = "уникальный идентификатор задачи")
                             @PathVariable Integer id,
                             @RequestBody @Valid TaskUpdateStatusReq request) {

        taskService.updateStatus(id, request);
    }

    @Operation(summary = "поиск",
            description = "Поиск задач - задачи ищутся по текстовому значению " +
                    "(по полям Наименование задачи) и с применением фильтров (по статусам задачи, по исполнителю, " +
                    "по автору задачи, по периоду крайнего срока задачи, по периоду создания задачи). " +
                    "Фильтры все не обязательны, как и текстовое поле. Результат будет отсортирован " +
                    "по дате создания задачи в обратном порядке (сначала свежие задачи).",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @GetMapping(value = "/v1/tasks",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TaskResp> getAllByFilter(
            @Parameter(description = "текстовая строка") @RequestParam(required = false) String query,
            @Parameter(description = "список статусов") @RequestParam(required = false) List<TaskStatus> statuses,
            @Parameter(description = "уникальный идентификатор автора") @RequestParam(required = false) Integer authorId,
            @Parameter(description = "уникальный идентификатор исполнителя") @RequestParam(required = false) Integer assigneesId,
            @Parameter(description = "минимальная дата+время создания") @RequestParam(required = false) Instant minCreatedDatetime,
            @Parameter(description = "максимальная дата+время создания") @RequestParam(required = false) Instant maxCreatedDatetime,
            @Parameter(description = "минимальная дата+время окончания") @RequestParam(required = false) Instant minDeadlineDatetime,
            @Parameter(description = "максимальная дата+время окончания") @RequestParam(required = false) Instant maxDeadlineDatetime
    ) {

        return taskService.getAllByParam(TaskFilterParam.builder()
                .query(query)
                .statuses(statuses)
                .authorId(authorId)
                .assigneesId(assigneesId)
                .minCreatedDatetime(minCreatedDatetime)
                .maxCreatedDatetime(maxCreatedDatetime)
                .minDeadlineDatetime(minDeadlineDatetime)
                .maxDeadlineDatetime(maxDeadlineDatetime)
                .build()
        );
    }
}
