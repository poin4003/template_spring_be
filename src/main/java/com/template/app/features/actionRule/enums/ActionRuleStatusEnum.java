package com.template.app.features.actionRule.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum ActionRuleStatusEnum implements BaseEnum {
    ACTIVE(1, "ACTIVE"),
    INACTIVE(2, "INACTIVE")
    ;
    
    private final int code;
    private final String description;

    ActionRuleStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ActionRuleStatusEnum fromCode(int code) {
        return EnumUtils.fromCode(ActionRuleStatusEnum.class, code);
    }
}
