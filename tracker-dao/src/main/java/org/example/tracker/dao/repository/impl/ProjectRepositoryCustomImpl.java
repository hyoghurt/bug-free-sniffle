package org.example.tracker.dao.repository.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.repository.ProjectRepositoryCustom;
import org.example.tracker.dto.project.ProjectFilterParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectRepositoryCustomImpl extends BaseCriteriaRepository implements ProjectRepositoryCustom {

    @Override
    public List<ProjectEntity> findByFilter(ProjectFilterParam filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> criteriaQuery = builder.createQuery(ProjectEntity.class);
        Root<ProjectEntity> root = criteriaQuery.from(ProjectEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        findInPredicate("status", filter.getStatuses(), root)
                .ifPresent(predicates::add);

        findOrLikeIgnoreCasePredicate(List.of("name", "code"), filter.getQuery(), builder, root)
                .ifPresent(predicates::add);

        criteriaQuery
                .select(root)
                .where(builder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    static Optional<Predicate> findOrLikeIgnoreCasePredicate(List<String> fields, String value, CriteriaBuilder builder,
                                                   Root<ProjectEntity> root) {
        List<Predicate> predicates = new ArrayList<>();

        for (String field : fields) {
            findLikeIgnoreCasePredicate(field, value, builder, root).ifPresent(predicates::add);
        }

        if (!predicates.isEmpty()) {
            Predicate predicate = builder.or(predicates.toArray(new Predicate[0]));
            return Optional.of(predicate);
        }

        return Optional.empty();
    }
}
