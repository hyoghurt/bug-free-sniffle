package org.example.tracker.dao.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.ObjectUtils;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.repository.ProjectFilterRepository;
import org.example.tracker.dto.project.ProjectFilterParam;
import org.example.tracker.dto.project.ProjectStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProjectFilterRepositoryCriteriaImpl implements ProjectFilterRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<ProjectEntity> findByFilter(ProjectFilterParam filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> criteriaQuery = builder.createQuery(ProjectEntity.class);
        Root<ProjectEntity> root = criteriaQuery.from(ProjectEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        findSearchPredicate(filter, builder, root).ifPresent(predicates::add);
        findStatusPredicate(filter, root).ifPresent(predicates::add);

        criteriaQuery.select(root)
                .where(
                        builder.and(
                                predicates.toArray(new Predicate[0])
                        )
                );

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private static Optional<Predicate> findStatusPredicate(ProjectFilterParam filter, Root<ProjectEntity> root) {
        List<ProjectStatus> statuses = filter.getStatuses();
        if (ObjectUtils.isNotEmpty(statuses)) {
            Predicate predicate = root.get("status").in(
                    statuses
            );
            return Optional.of(predicate);
        }
        return Optional.empty();
    }

    private static Optional<Predicate> findSearchPredicate(ProjectFilterParam filter, CriteriaBuilder builder,
                                                           Root<ProjectEntity> root) {
        String search = filter.getQuery();
        if (search != null && !search.isBlank()) {
            final String value = "%" + search.toUpperCase() + "%";
            Predicate predicate = builder.or(
                    builder.like(
                            builder.upper(root.get("code")), value
                    ),
                    builder.like(
                            builder.upper(root.get("name")), value
                    )
            );
            return Optional.of(predicate);
        }
        return Optional.empty();
    }
}
