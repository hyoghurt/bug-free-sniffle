package org.example.tracker.dao.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.tracker.dto.task.TaskStatus;
import org.hibernate.Hibernate;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer projectId;
    private String title;
    private String description;
    private Integer assigneesId;
    private Integer authorId;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.OPEN;

    private Integer laborCostsInHours;
    private Instant createdDatetime = Instant.now();
    private Instant updateDatetime;
    private Instant deadlineDatetime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TaskEntity that = (TaskEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
