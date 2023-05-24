package org.example.tracker.controller;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/v1/employees",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EmployeeResp create(@RequestBody @Valid EmployeeReq request) {
        return employeeService.create(request);
    }

    @PutMapping(value = "/v1/employees/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EmployeeResp update(@PathVariable Integer id, @RequestBody @Valid EmployeeReq request) {
        return employeeService.update(id, request);
    }

    @DeleteMapping("/v1/employees/{id}")
    public void delete(@PathVariable Integer id) {
        employeeService.delete(id);
    }

    @GetMapping(value = "/v1/employees/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EmployeeResp getById(@PathVariable Integer id) {
        return employeeService.getById(id);
    }

    @GetMapping(value = "/v1/employees",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmployeeResp> getAllByQuery(@RequestParam(required = false) String query) {
        return employeeService.getAllByQuery(query);
    }
}
