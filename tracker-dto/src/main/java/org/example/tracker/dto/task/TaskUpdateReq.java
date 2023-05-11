package org.example.tracker.dto.task;


import java.time.Instant;

// изменить задачу
public class TaskUpdateReq {
    private int id; //required
    private int projectId; //required
    private String title; //required
    private String description;
    private Integer assigneesId;
    private long laborCostsInMillis; //required
    private Instant deadlineDatetime; //required
}
