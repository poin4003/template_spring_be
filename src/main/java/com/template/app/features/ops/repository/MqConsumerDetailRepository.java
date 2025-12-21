package com.template.app.features.ops.repository;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.ops.entity.MqConsumerDetailEntity;

@Mapper
public interface MqConsumerDetailRepository extends BaseMapper<MqConsumerDetailEntity> {
    
}
