package com.app.features.rbac.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.features.rbac.entity.RoleEntity;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    @Query("SELECT r FROM RoleEntity r JOIN r.users u WHERE u.id = :userId")
    List<RoleEntity> findByUserId(@Param("userId") UUID userId);
}
