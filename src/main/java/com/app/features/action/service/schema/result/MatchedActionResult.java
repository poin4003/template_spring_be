package com.app.features.action.service.schema.result;

import com.app.features.action.enums.ActionTypeEnum;
import com.app.features.action.vo.BaseActionConfig;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchedActionResult {
    
    private ActionTypeEnum actionType;

    private BaseActionConfig actionConfig;

    private String name;

    private Integer priority;
}
