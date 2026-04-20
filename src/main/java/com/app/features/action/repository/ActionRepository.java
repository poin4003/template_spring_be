package com.app.features.action.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.features.action.entity.ActionEntity;
import com.app.features.error.enums.ErrorCategoryEnum;

@Repository
public interface ActionRepository extends JpaRepository<ActionEntity, UUID> {

        @Query("""
                        SELECT a FROM ActionEntity a
                        JOIN a.errors e
                        WHERE a.targetKey = :targetKey
                          AND e.code = :errorCode
                          AND a.status = ActionStatusEnum.ACTIVE
                        ORDER BY a.priority DESC
                        """)
        List<ActionEntity> findMatchedActionsLimit(
                        @Param("targetKey") String targetKey,
                        @Param("errorCode") Integer errorCode,
                        @Param("category") ErrorCategoryEnum category,
                        Pageable pageable);

        default Optional<ActionEntity> findTopMatchedAction(String targetKey, Integer errorCode,
                        ErrorCategoryEnum category) {
                List<ActionEntity> results = findMatchedActionsLimit(
                                targetKey, errorCode, category, PageRequest.of(0, 1));

                return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }
}
