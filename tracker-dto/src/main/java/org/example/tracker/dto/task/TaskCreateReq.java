package org.example.tracker.dto.task;


import java.time.Instant;

// создать задачу
public class TaskCreateReq {
    private int projectId; //required
    private String title; //required
    private String description;
    private Integer assigneesId;
    private long laborCostsInMillis; //required
    private Instant deadlineDatetime; //required
}
