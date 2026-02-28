package com.template.app.features.action.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum TargetTypeEnum implements BaseEnum {
    GLOBAL(0, "GLOBAL"),
    MQ_CONSUMER(1, "MQ_CONSUMER"),
    WEBHOOK(2, "WEBHOOK"),
    API(3, "API")
    ;
    
    private final int code;
    private final String description;

    TargetTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TargetTypeEnum fromCode(int code) {
        return EnumUtils.fromCode(TargetTypeEnum.class, code);
    }
}
