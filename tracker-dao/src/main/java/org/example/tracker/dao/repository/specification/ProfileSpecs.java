package org.example.tracker.dao.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.tracker.dao.entity.ProjectEntity;
import org.example.tracker.dao.entity.ProjectEntity_;
import org.example.tracker.dto.project.ProjectFilterParam;
import org.example.tracker.dto.project.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ProfileSpecs {

    public static Specification<ProjectEntity> byFilterParam(final ProjectFilterParam param) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String search = param.getQuery();
            if (search != null && !search.isBlank()) {
                String reg = "%" + search.toUpperCase() + "%";
                predicates.add(cb
                        .or(
                                cb.like(cb.upper(root.get(ProjectEntity_.code)), reg),
                                cb.like(cb.upper(root.get(ProjectEntity_.code)), reg)
                        ));
            }

            List<ProjectStatus> statuses = param.getStatuses();
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get(ProjectEntity_.status).in(statuses));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ProjectEntity> byCodeLikeIgnoreCase(final String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            return cb.like(cb.upper(root.get(ProjectEntity_.code)), "%" + search.toUpperCase() + "%");
        };
    }

    public static Specification<ProjectEntity> byNameLikeIgnoreCase(final String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            return cb.like(cb.upper(root.get(ProjectEntity_.name)), "%" + search.toUpperCase() + "%");
        };
    }

    public static Specification<ProjectEntity> byStatusIn(final Collection<ProjectStatus> collection) {
        return (root, query, cb) -> {
            if (collection == null || collection.isEmpty()) return null;
            return root.get(ProjectEntity_.status).in(collection);
        };
    }
}
