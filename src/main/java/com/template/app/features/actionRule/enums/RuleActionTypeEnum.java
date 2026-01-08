package com.template.app.features.actionRule.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum RuleActionTypeEnum implements BaseEnum {
    RETRY(1, "RETRY"),
    DLQ(2, "DEAD_LETTER_QUEUE"),
    IGNORE(3, "IGNORE"),
    FAIL_FAST(4, "FAIL_FAST"),
    ALERT(5, "ALERT")
    ;

    private final int code;
    private final String description;

    RuleActionTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RuleActionTypeEnum fromCode(int code) {
        return EnumUtils.fromCode(RuleActionTypeEnum.class, code);
    }
}
