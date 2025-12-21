package com.template.app.features.rbac.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.rbac.entity.PermissionEntity;

@Mapper
public interface PermissionRepository extends BaseMapper<PermissionEntity> {

    List<PermissionEntity> selectPermissionByUserId(@Param("userId") UUID userId);

    List<PermissionEntity> selectPermissionByRoleId(@Param("roleId") UUID roleId);

}
