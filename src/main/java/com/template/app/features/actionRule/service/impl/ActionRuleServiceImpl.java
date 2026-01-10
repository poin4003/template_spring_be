package com.template.app.features.actionRule.service.impl;

import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.template.app.features.actionRule.entity.ActionRuleEntity;
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

        ActionRuleEntity rule = actionRuleRepo.findMatchedRule(targetId, errorCode, category);

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
