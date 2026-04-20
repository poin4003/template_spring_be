package com.app.features.action.service.schema.command;

import com.app.features.action.enums.ActionTypeEnum;
import com.app.features.action.enums.TargetTypeEnum;
import com.app.features.action.vo.BaseActionConfig;
import com.app.features.error.enums.ErrorCategoryEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateActionCmd {
    // TODO: refactor to pipeliner 
    @NotBlank(message = "Rule name is required")
    private String ruleName;

    @NotNull
    private TargetTypeEnum targetType;

    private String targetKey;

    private ErrorCategoryEnum matchCategory;

    private Integer matchErrorCode;

    @NotNull
    private ActionTypeEnum actionType;

    @NotNull
    @Valid
    private BaseActionConfig actionConfig;

    private Integer priority = 0;
}
