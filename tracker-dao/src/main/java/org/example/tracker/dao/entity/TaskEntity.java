package org.example.tracker.dao.entity;


import org.example.tracker.dto.task.TaskStatus;

import java.time.Instant;

public class TaskEntity {
    private int id; //fk
    private int projectId; //required
    private String title; //required
    private String description;
    private Integer assigneesId; //has a rule
    private int authorId; //has a rule
    private TaskStatus status; //required
    private long laborCostsInMillis; //required трудозатраты в миллисекундах
    private Instant createdDatetime; //required
    private Instant updateDatetime;
    private Instant deadlineDatetime; //required, has a rule
}
