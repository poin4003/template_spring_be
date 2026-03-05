package com.app.features.action.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.features.action.entity.ActionEntity;
import com.app.features.error.enums.ErrorCategoryEnum;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ActionRepository extends JpaRepository<ActionEntity, UUID> {

    @Query("""
                SELECT DISTINCT a FROM ActionEntity a
                JOIN a.errors e
                WHERE a.status = com.app.features.action.enums.ActionStatusEnum.ACTIVE
                  AND (a.targetId = :targetId OR a.targetType = com.app.features.action.enums.TargetTypeEnum.ALL)
                  AND e.code = :errorCode
                  AND e.category = :category
                ORDER BY a.priority DESC, a.targetType DESC
            """)
    List<ActionEntity> findMatchedActions(
            @Param("targetId") UUID targetId,
            @Param("errorCode") Integer errorCode,
            @Param("category") ErrorCategoryEnum category);

    default Optional<ActionEntity> findTopMatchedAction(UUID targetId, Integer errorCode, ErrorCategoryEnum category) {
        return findMatchedActions(targetId, errorCode, category).stream().findFirst();
    }
}
