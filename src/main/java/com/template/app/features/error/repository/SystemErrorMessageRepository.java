package com.template.app.features.error.repository;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.error.entity.SystemErrorMessageEntity;

@Mapper
public interface SystemErrorMessageRepository extends BaseMapper<SystemErrorMessageEntity> {
    
}
