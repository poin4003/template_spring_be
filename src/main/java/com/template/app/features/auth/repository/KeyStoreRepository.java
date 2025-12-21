package com.template.app.features.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.auth.entity.KeyStoreEntity;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KeyStoreRepository extends BaseMapper<KeyStoreEntity> {
    void upsert(KeyStoreEntity entity);
}
