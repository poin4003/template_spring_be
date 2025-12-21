package com.template.app.features.auth.repository;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.auth.entity.ConsumedRefreshTokenEntity;

@Mapper
public interface ConsumedRefreshTokenRepository extends BaseMapper<ConsumedRefreshTokenEntity> {
    
}
