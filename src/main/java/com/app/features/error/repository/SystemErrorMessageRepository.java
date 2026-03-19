package com.app.features.error.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.features.error.entity.SystemErrorMessageEntity;

@Repository
public interface SystemErrorMessageRepository extends JpaRepository<SystemErrorMessageEntity, UUID> {
    List<SystemErrorMessageEntity> findByErrorDefinitionIdIn(Collection<UUID> ids);

    boolean existsByErrorDefinitionIdAndLanguageCode(UUID errorId, String language);
}
