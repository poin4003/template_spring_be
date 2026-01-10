package com.template.app.features.actionRule.repository;

import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.template.app.features.actionRule.entity.ActionRuleEntity;
import com.template.app.features.error.enums.ErrorCategoryEnum;

@Mapper
public interface ActionRuleRepository extends BaseMapper<ActionRuleEntity> {
    ActionRuleEntity findMatchedRule(
        @Param("targetId") UUID targetId, 
        @Param("errorCode") Integer errorCode,
        @Param("category") ErrorCategoryEnum category
    );
}
