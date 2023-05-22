package org.example.tracker.dao.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.tracker.dto.team.EmployeeRole;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TeamEmbeddable {
    @OneToOne
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;

    @Enumerated(EnumType.STRING)
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