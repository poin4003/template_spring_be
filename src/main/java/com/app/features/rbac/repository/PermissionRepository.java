package com.app.features.rbac.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.features.rbac.entity.PermissionEntity;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {
    @Query("SELECT DISTINCT p FROM PermissionEntity p JOIN p.roles r JOIN r.users u WHERE u.id = :userId")
    List<PermissionEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM PermissionEntity p JOIN p.roles r WHERE r.id = :roleId")
    List<PermissionEntity> findByRoleId(@Param("roleId") UUID roleId);
}
