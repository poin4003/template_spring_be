package com.template.app.features.action.service.schema.result;

import com.template.app.features.action.enums.ActionTypeEnum;
import com.template.app.features.action.vo.BaseActionConfig;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchedActionResult {
    
    private ActionTypeEnum actionType;

    private BaseActionConfig actionConfig;

    private String ruleName;

    private Integer priority;
}
