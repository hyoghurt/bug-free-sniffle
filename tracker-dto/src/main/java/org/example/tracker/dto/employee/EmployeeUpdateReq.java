package org.example.tracker.dto.employee;

import lombok.Data;

// изменение сотрудника
@Data
public class EmployeeUpdateReq {
    private int id; //required
    private String firstName; //required
    private String lastName; //required
    private String middleName;
    private String position;
    private String upn;
    private String email;
}
