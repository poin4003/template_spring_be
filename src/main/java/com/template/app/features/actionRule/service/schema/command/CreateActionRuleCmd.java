package com.template.app.features.actionRule.service.schema.command;

import java.util.UUID;

import com.template.app.features.actionRule.enums.RuleActionTypeEnum;
import com.template.app.features.actionRule.enums.RuleTargetTypeEnum;
import com.template.app.features.actionRule.vo.ActionConfig;
import com.template.app.features.error.enums.ErrorCategoryEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateActionRuleCmd {
    
    @NotBlank(message = "Rule name is required")
    private String ruleName;

    @NotNull
    private RuleTargetTypeEnum targetType;

    private UUID targetId;

    private ErrorCategoryEnum matchCategory;

    private Integer matchErrorCode;

    @NotNull
    private RuleActionTypeEnum actionType;

    @NotNull
    @Valid
    private ActionConfig actionConfig;

    private Integer priority = 0;
}
