package com.app.features.action.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.features.action.entity.ActionEntity;
import com.app.features.error.enums.ErrorCategoryEnum;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ActionRepository extends JpaRepository<ActionEntity, UUID> {

    @Query(value = """
            SELECT * FROM actions
            WHERE user_id = :userId
            AND some_count_column = :count
            AND category = :#{#category.code}
            """, nativeQuery = true)
    List<ActionEntity> findMatchedActionsLimit(
            @Param("userId") UUID userId,
            @Param("count") Integer count,
            @Param("category") ErrorCategoryEnum category,
            Pageable pageable);

    default Optional<ActionEntity> findTopMatchedAction(UUID targetId, Integer errorCode, ErrorCategoryEnum category) {
        List<ActionEntity> results = findMatchedActionsLimit(
                targetId, errorCode, category, PageRequest.of(0, 1));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
