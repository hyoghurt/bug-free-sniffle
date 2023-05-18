package org.example.tracker.dto.employee;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class EmployeeResp extends EmployeeReq {
    private int id;
    private EmployeeStatus status;
}
