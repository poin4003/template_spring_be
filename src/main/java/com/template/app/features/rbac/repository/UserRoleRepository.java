package com.template.app.features.rbac.repository;

import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.rbac.entity.UserRoleEntity;

@Mapper
public interface UserRoleRepository extends BaseMapper<UserRoleEntity> {
    int assignRoleToUser(@Param("userId") UUID userId, @Param("roleId") UUID roleId);

    int removeRoleFromUser(@Param("userId") UUID userId, @Param("roleId") UUID roleId);
    
    int deleteRolesByUserId(@Param("userId") UUID userId);
}
