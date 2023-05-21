package org.example.tracker.dto.task;


import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
public class TaskReq {
    private int projectId; //required
    private String title; //required
    private String description;
    private Integer assigneesId;
    private int laborCostsInHours; //required
    private Instant deadlineDatetime; //required
}
