package org.example.tracker;

import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dto.employee.EmployeeStatus;

import java.util.List;

public class EmployeeFilter {
    public static List<EmployeeEntity> filter(List<EmployeeEntity> entities, String search) {
        return entities.stream().filter(e ->
                (
                        search == null
                                || e.getFirstName().toUpperCase().contains(search.toUpperCase())
                                || e.getLastName().toUpperCase().contains(search.toUpperCase())
                                || (e.getMiddleName() != null && e.getMiddleName().toUpperCase().contains(search.toUpperCase()))
                                || (e.getEmail() != null && e.getEmail().toUpperCase().contains(search.toUpperCase()))
                                || (e.getUpn() != null && e.getUpn().toUpperCase().contains(search.toUpperCase()))
                ) && (
                        e.getStatus().equals(EmployeeStatus.ACTIVE)
                )
        ).toList();
    }
}