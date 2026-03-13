package com.app.features.error.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.features.error.entity.SystemErrorDefinitionEntity;


@Repository
public interface SystemErrorDefinitionRepository extends JpaRepository<SystemErrorDefinitionEntity, UUID> {
    Optional<SystemErrorDefinitionEntity> findByCode(Integer code);

    boolean existsByCode(Integer code);
}
