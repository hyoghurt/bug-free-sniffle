package org.example.tracker.dao.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.entity.EmployeeEntity_;
import org.example.tracker.dto.employee.EmployeeFilterParam;
import org.example.tracker.dto.employee.EmployeeStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class EmployeeSpecs {

    public static Specification<EmployeeEntity> byFilterParam(final EmployeeFilterParam param) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String search = param.getQuery();
            if (search != null && !search.isBlank()) {
                String reg = "%" + search.toUpperCase() + "%";
                predicates.add(cb
                        .or(
                                cb.like(cb.upper(root.get(EmployeeEntity_.firstName)), reg),
                                cb.like(cb.upper(root.get(EmployeeEntity_.lastName)), reg),
                                cb.like(cb.upper(root.get(EmployeeEntity_.middleName)), reg),
                                cb.like(cb.upper(root.get(EmployeeEntity_.upn)), reg),
                                cb.like(cb.upper(root.get(EmployeeEntity_.email)), reg)
                        ));
            }

            predicates.add(cb.equal(root.get(EmployeeEntity_.status), EmployeeStatus.ACTIVE));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
