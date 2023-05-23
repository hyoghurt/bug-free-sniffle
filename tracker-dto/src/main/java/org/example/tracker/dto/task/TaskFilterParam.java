package org.example.tracker.dto.task;

import java.time.Instant;
import java.util.List;

public class TaskFilterParam {
    private String query;
    private List<TaskStatus> statuses;
    private Integer assigneesId;
    private Instant minCreatedDatetime;
    private Instant maxCreatedDatetime;
    private Instant minDeadlineDatetime;
    private Instant maxDeadlineDatetime;
}
