package com.app.features.rbac.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.features.rbac.entity.PermissionEntity;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface PermissionRepository
                extends JpaRepository<PermissionEntity, UUID>, JpaSpecificationExecutor<PermissionEntity> {

        @Query(value = """
                        SELECT p.* FROM permission p
                        INNER JOIN role_permissions rp ON p.id = rp.permission_id
                        WHERE rp.role_id = :roleId
                        """, nativeQuery = true)
        List<PermissionEntity> findByRoleId(@Param("roleId") UUID roleId);

        @Query(value = """
                        SELECT DISTINCT p.* FROM permission p
                        INNER JOIN role_permissions rp ON p.id = rp.permission_id
                        INNER JOIN user_roles ur ON rp.role_id = ur.role_id
                        WHERE ur.user_id = :userId
                        """, nativeQuery = true)
        List<PermissionEntity> findByUserId(@Param("userId") UUID userId);

        boolean existsByKey(String key);
}
