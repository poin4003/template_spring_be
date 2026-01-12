package com.template.app.features.action.service.schema.command;

import java.util.UUID;

import com.template.app.features.action.enums.ActionTypeEnum;
import com.template.app.features.action.enums.TargetTypeEnum;
import com.template.app.features.action.vo.BaseActionConfig;
import com.template.app.features.error.enums.ErrorCategoryEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateActionCmd {
    
    @NotBlank(message = "Rule name is required")
    private String ruleName;

    @NotNull
    private TargetTypeEnum targetType;

    private UUID targetId;

    private ErrorCategoryEnum matchCategory;

    private Integer matchErrorCode;

    @NotNull
    private ActionTypeEnum actionType;

    @NotNull
    @Valid
    private BaseActionConfig actionConfig;

    private Integer priority = 0;
}
