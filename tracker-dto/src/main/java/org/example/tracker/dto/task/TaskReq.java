package org.example.tracker.dto.task;


import java.time.Instant;

// создать задачу
public class TaskReq {
    private int projectId; //required
    private String title; //required
    private String description;
    private Integer assigneesId;
    private long laborCostsInHours; //required
    private Instant deadlineDatetime; //required
}
