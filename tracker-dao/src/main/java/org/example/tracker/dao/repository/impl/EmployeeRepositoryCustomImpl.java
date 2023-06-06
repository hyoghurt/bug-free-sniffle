package org.example.tracker.dao.repository.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.tracker.dao.entity.EmployeeEntity;
import org.example.tracker.dao.repository.EmployeeRepositoryCustom;
import org.example.tracker.dto.employee.EmployeeStatus;

import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryCustomImpl extends BaseCriteriaRepository implements EmployeeRepositoryCustom {

    @Override
    public List<EmployeeEntity> findAllByQuery(String query) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmployeeEntity> criteriaQuery = builder.createQuery(EmployeeEntity.class);
        Root<EmployeeEntity> root = criteriaQuery.from(EmployeeEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        Predicate statusPredicate = findEqualPredicate("status", EmployeeStatus.ACTIVE, builder, root)
                .orElseThrow(() -> new RuntimeException("error where status = ACTIVE"));
        predicates.add(statusPredicate);

        findOrLikeIgnoreCasePredicate(List
                .of("firstName", "lastName", "middleName", "upn", "email"), query, builder, root)
                .ifPresent(predicates::add);

        criteriaQuery
                .select(root)
                .where(builder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
