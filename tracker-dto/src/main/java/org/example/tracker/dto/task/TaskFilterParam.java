package org.example.tracker.dto.task;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class TaskFilterParam {
    private String query;
    private List<TaskStatus> statuses;
    private Integer authorId;
    private Integer assigneesId;
    private Instant minCreatedDatetime;
    private Instant maxCreatedDatetime;
    private Instant minDeadlineDatetime;
    private Instant maxDeadlineDatetime;
}
