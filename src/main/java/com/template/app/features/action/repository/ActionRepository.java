package com.template.app.features.action.repository;

import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.action.entity.ActionEntity;
import com.template.app.features.error.enums.ErrorCategoryEnum;

@Mapper
public interface ActionRepository extends BaseMapper<ActionEntity> {
    ActionEntity findMatchedAction(
        @Param("targetId") UUID targetId, 
        @Param("errorCode") Integer errorCode,
        @Param("category") ErrorCategoryEnum category
    );
}
