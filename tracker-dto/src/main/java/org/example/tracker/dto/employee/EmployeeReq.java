package org.example.tracker.dto.employee;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class EmployeeReq {
    private String firstName; //required
    private String lastName; //required
    private String middleName;
    private String position;
    private String upn;
    private String email;
}
