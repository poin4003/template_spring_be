package com.template.app.features.ops.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.ops.entity.CronJobConfigEntity;
import com.template.app.features.ops.enums.EndpointStatusEnum;

@Mapper
public interface CronJobConfigRepository extends BaseMapper<CronJobConfigEntity> {
    List<CronJobConfigEntity> findAllActiveJobs(@Param("status") EndpointStatusEnum status);
}
