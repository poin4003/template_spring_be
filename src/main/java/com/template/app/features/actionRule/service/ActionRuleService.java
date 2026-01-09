package com.template.app.features.actionRule.service;

import java.util.UUID;

import com.template.app.features.actionRule.service.schema.result.MatchedRuleResult;
import com.template.app.features.error.enums.ErrorCategoryEnum;

public interface ActionRuleService {
    MatchedRuleResult findBestMatch(UUID targetId, Integer errorCode, ErrorCategoryEnum category);
}
