package org.example.tracker.dto.employee;


import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class EmployeeResp extends EmployeeReq {
    private int id;
    private EmployeeStatus status;
}
