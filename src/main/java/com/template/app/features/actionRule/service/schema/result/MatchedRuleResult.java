package com.template.app.features.actionRule.service.schema.result;

import com.template.app.features.actionRule.enums.RuleActionTypeEnum;
import com.template.app.features.actionRule.vo.ActionConfig;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchedRuleResult {
    
    private RuleActionTypeEnum actionType;

    private ActionConfig actionConfig;

    private String ruleName;

    private Integer priority;
}
