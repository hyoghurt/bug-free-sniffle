package org.example.tracker.dto.task;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskUpdateStatusReq {
    private TaskStatus status;
}
