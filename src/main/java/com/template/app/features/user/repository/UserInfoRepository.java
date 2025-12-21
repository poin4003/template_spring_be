package com.template.app.features.user.repository;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.user.entity.UserInfoEntity;

@Mapper
public interface UserInfoRepository extends BaseMapper<UserInfoEntity> {
    
}
