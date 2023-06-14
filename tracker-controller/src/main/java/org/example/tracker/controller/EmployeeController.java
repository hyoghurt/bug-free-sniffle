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
import org.example.tracker.dto.employee.EmployeeFilterParam;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.error.ErrorResp;
import org.example.tracker.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "employee", description = "управление сотрудниками")
@SecurityRequirement(name = "basicScheme")
@ApiResponse(responseCode = "401", content = @Content)
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Operation(summary = "создание",
            description = "Создать профиль сотрудника с набором атрибутов. Статус сотрудника становится Активный.",
            responses = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EmployeeResp create(@RequestBody @Valid EmployeeReq request) {

        return employeeService.create(request);
    }

    @Operation(summary = "изменение",
            description = "Изменение сотрудника. Удаленного сотрудника изменить нельзя.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class))),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EmployeeResp update(@Parameter(description = "уникальный идентификатор сотрудника")
                               @PathVariable Integer id,
                               @RequestBody @Valid EmployeeReq request) {

        return employeeService.update(id, request);
    }

    @Operation(summary = "удаление",
            description = "Удаление сотрудника. При удалении сотрудника, сотрудник переводится в статус DELETED",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "уникальный идентификатор сотрудника")
                       @PathVariable Integer id) {

        employeeService.delete(id);
    }

    @Operation(summary = "получение",
            description = "Получение карточки сотрудника по ID",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ErrorResp.class)))
            })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmployeeResp getById(@Parameter(description = "уникальный идентификатор сотрудника")
                                @PathVariable Integer id) {

        return employeeService.getById(id);
    }

    @Operation(summary = "поиск",
            description = "Поиск сотрудников. Поиск осуществляется по текстовому значению, " +
                    "которое проверяется по атрибутам: Фамилия, Имя, Отчество, учетная запись, электронная почта. " +
                    "Если параметр пустой, то вернуться все сотрудники. В ответе только сотрудники со статусом ACTIVE.",
            responses = {
                    @ApiResponse(responseCode = "200")
            })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmployeeResp> getAllByParam(@Parameter(description = "текстовое значение для поиска")
                                            @RequestParam(required = false) String query) {

        return employeeService.getAllByParam(new EmployeeFilterParam(query));
    }
}
