package com.template.app.features.actionRule.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum RuleTargetTypeEnum implements BaseEnum {
    GLOBAL(0, "GLOBAL"),
    MQ_CONSUMER(1, "MQ_CONSUMER"),
    WEBHOOK(2, "WEBHOOK"),
    API(3, "API")
    ;
    
    private final int code;
    private final String description;

    RuleTargetTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RuleTargetTypeEnum fromCode(int code) {
        return EnumUtils.fromCode(RuleTargetTypeEnum.class, code);
    }
}
