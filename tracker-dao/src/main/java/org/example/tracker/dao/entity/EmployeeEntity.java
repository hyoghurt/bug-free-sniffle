package org.example.tracker.dao.entity;

import org.example.tracker.dao.enums.EmployeeStatus;

public class EmployeeEntity {
    private int id; //fk
    private String firstName; //required
    private String lastName; //required
    private String middleName;
    private String position;
    private String uz; //TODO учетная запись что это
    private String email;
    private EmployeeStatus status; //required
}
