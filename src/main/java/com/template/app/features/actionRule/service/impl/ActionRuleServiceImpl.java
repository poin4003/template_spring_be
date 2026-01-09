package com.template.app.features.actionRule.service.impl;

import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.template.app.features.actionRule.entity.ActionRuleEntity;
import com.template.app.features.actionRule.enums.ActionRuleStatusEnum;
import com.template.app.features.actionRule.enums.RuleTargetTypeEnum;
import com.template.app.features.actionRule.repository.ActionRuleRepository;
import com.template.app.features.actionRule.service.ActionRuleService;
import com.template.app.features.actionRule.service.schema.result.MatchedRuleResult;
import com.template.app.features.error.enums.ErrorCategoryEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionRuleServiceImpl implements ActionRuleService {
    
    private final ActionRuleRepository actionRuleRepo;

    @Override
    @Cacheable(
        value = "action_rules", 
        key = "#targetId + '_' + #errorCode + '_' + (#category != null ? #category.name() : 'NULL')",
        unless = "#result == null" 
    )
    public MatchedRuleResult findBestMatch(UUID targetId, Integer errorCode, ErrorCategoryEnum category) {
        log.debug("Matching rule for Target: {}, Code: {}, Cat: {}", targetId, errorCode, category);

        ActionRuleEntity rule = actionRuleRepo.selectOne(new LambdaQueryWrapper<ActionRuleEntity>()
            .select(ActionRuleEntity::getRuleName, ActionRuleEntity::getActionType,
                ActionRuleEntity::getActionConfig, ActionRuleEntity::getPriority
            )
            .eq(ActionRuleEntity::getStatus, ActionRuleStatusEnum.ACTIVE)
            .and(w -> w.eq(ActionRuleEntity::getTargetId, targetId)
                .or()
                .eq(ActionRuleEntity::getTargetType, RuleTargetTypeEnum.GLOBAL)
            )
            .and(w -> w
                .eq(ActionRuleEntity::getMatchCategory, errorCode)
                .or()
                .eq(ActionRuleEntity::getMatchCategory, category)
                .or(sub -> sub
                    .isNull(ActionRuleEntity::getMatchErrorCode)
                    .isNull(ActionRuleEntity::getMatchCategory)
                )
            )
            .orderByDesc(ActionRuleEntity::getPriority)
            .orderByDesc(ActionRuleEntity::getTargetType)
            .last("LIMIT 1")
        );

        if (rule == null) {
            return null;
        }

        return MatchedRuleResult.builder()
            .ruleName(rule.getRuleName())
            .actionType(rule.getActionType())
            .actionConfig(rule.getActionConfig())
            .priority(rule.getPriority())
            .build();
    }
}
