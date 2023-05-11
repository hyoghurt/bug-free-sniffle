package org.example.tracker.dto.task;


import java.time.Instant;

public class TaskResp {
    private int id;
    private int projectId;
    private String title;
    private String description;
    private Integer assigneesId;
    private int authorId;
    private TaskStatus status;
    private long laborCostsInMillis;
    private Instant createdDatetime;
    private Instant updateDatetime;
    private Instant deadlineDatetime;
}
