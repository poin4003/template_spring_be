package com.template.app.features.rbac.repository;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.rbac.entity.RolePermissionEntity;

import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolePermissionRepository extends BaseMapper<RolePermissionEntity> {
    int assignPermissionToRole(@Param("roleId") UUID roleId, @Param("permissionId") UUID permissionId);
    
    int deletePermissionsByRoleId(@Param("roleId") UUID roleId);
}
