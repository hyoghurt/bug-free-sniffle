package org.example.tracker.dto.employee;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class EmployeeResp extends EmployeeReq {
    private int id;
    private EmployeeStatus status;
}
