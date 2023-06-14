package org.example.tracker.dto.project;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateStatusReq {

    @NotNull(message = "status required")
    private ProjectStatus status;
}
