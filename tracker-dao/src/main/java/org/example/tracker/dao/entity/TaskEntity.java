package org.example.tracker.dao.entity;


import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import lombok.*;
import org.example.tracker.dto.task.TaskStatus;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

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

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "assignees_id")
    private EmployeeEntity assignees;

    private Integer authorId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Type(value = PostgreSQLEnumType.class)
    @Column(columnDefinition = "task_status")
    private TaskStatus status = TaskStatus.OPEN;

    private Integer laborCostsInHours;

    @Builder.Default
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
