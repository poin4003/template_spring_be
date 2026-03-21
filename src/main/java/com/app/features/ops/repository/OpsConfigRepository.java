package com.app.features.ops.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.features.ops.entity.OpsConfigEntity;
import com.app.features.ops.enums.OpsStatusEnum;
import com.app.features.ops.enums.OpsTypeEnum;

@Repository
public interface OpsConfigRepository extends JpaRepository<OpsConfigEntity, UUID> {
    List<OpsConfigEntity> findByTypeAndStatus(OpsTypeEnum type, OpsStatusEnum status);

    List<OpsConfigEntity> findByStatus(OpsStatusEnum status);

    Optional<OpsConfigEntity> findFirstByNameAndType(String name, OpsTypeEnum type);
}
