package org.example.tracker.dao.entity;


import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import lombok.*;
import org.example.tracker.dto.team.EmployeeRole;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TeamEmbeddable {
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;

    @Enumerated(EnumType.STRING)
    @Type(value = PostgreSQLEnumType.class)
    @Column(columnDefinition = "employee_role")
    private EmployeeRole role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TeamEmbeddable that = (TeamEmbeddable) o;
        return getEmployee() != null && Objects.equals(getEmployee(), that.getEmployee())
                && getRole() != null && Objects.equals(getRole(), that.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, role);
    }
}
