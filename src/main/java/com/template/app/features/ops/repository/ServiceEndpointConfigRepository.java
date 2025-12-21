package com.template.app.features.ops.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.ops.entity.ServiceEndpointConfigEntity;

@Mapper
public interface ServiceEndpointConfigRepository extends BaseMapper<ServiceEndpointConfigEntity>{
    List<ServiceEndpointConfigEntity> findActiveMqConfigs();
}
