package org.example.tracker.dto.task;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateStatusReq {
    @NotNull(message = "status required")
    private TaskStatus status;
}
