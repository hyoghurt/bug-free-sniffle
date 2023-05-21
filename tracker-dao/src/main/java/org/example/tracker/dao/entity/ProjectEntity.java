package org.example.tracker.dao.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.tracker.dto.project.ProjectStatus;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.DRAFT;

    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "project_id"), name = "teams")
    private Set<TeamEmbeddable> teams = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProjectEntity that = (ProjectEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
