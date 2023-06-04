package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.helper.EmployeeFilter;
import org.example.tracker.dao.repository.specification.EmployeeSpecs;
import org.example.tracker.dto.employee.EmployeeFilterParam;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeRepositoryTest extends Base {

    @BeforeEach
    void setUp() {
        employeeEntities = initEmployeeEntities();
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    void employeeFilter() {
        equalsFilter(null);
        equalsFilter("tEst");
        equalsFilter("");
    }

    void equalsFilter(final String query) {
        EmployeeFilterParam param = EmployeeFilterParam.builder()
                .query(query)
                .build();

        List<EmployeeEntity> expected = employeeEntities.stream()
                .filter(e -> EmployeeFilter.filter(e, param))
                .toList();

        List<EmployeeEntity> actual = employeeRepository
                .findAll(EmployeeSpecs.byFilterParam(param));

        assertEquals(expected, actual);
    }
}