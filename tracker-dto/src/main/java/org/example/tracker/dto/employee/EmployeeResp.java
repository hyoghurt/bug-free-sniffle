package org.example.tracker.dto.employee;


import lombok.Data;

@Data
public class EmployeeResp {
    private int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String position;
    private String upn;
    private String email;
    private EmployeeStatus status;
}
