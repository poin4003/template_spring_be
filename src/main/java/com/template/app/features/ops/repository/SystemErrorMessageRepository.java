package com.template.app.features.ops.repository;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.ops.entity.SystemErrorMessageEntity;

@Mapper
public interface SystemErrorMessageRepository extends BaseMapper<SystemErrorMessageEntity> {
    
}
