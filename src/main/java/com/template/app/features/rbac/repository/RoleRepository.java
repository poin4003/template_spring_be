package com.template.app.features.rbac.repository;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.rbac.entity.RoleEntity;

@Mapper
public interface RoleRepository extends BaseMapper<RoleEntity> {

    List<RoleEntity> selectRolesByUserId(@Param("userId") UUID userId);
    
}
