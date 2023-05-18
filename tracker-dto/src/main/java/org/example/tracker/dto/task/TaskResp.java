package org.example.tracker.dto.task;


import java.time.Instant;

public class TaskResp extends TaskReq {
    private int id;
    private int authorId;
    private TaskStatus status;
    private Instant createdDatetime;
    private Instant updateDatetime;
}
