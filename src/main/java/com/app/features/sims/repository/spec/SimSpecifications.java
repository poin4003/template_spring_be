package com.app.features.sims.repository.spec;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.app.features.sims.entity.SimEntity;
import com.app.features.sims.filter.SimFilterCriteria;

import jakarta.persistence.criteria.Predicate;

public class SimSpecifications {

    public static Specification<SimEntity> withFilter(SimFilterCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getPhoneNumber() != null && !criteria.getPhoneNumber().trim().isEmpty()) {
                predicates.add(cb.like(root.get("phoneNumber"), "%" + criteria.getPhoneNumber() + "%"));
            }

            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getFromDate()));
            }

            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getToDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
