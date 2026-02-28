package com.template.app.features.action.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum ActionTypeEnum implements BaseEnum {
    MQ_RETRY(1, "MQ_RETRY"),
    MQ_DLQ(2, "MQ_DLQ"),
    ALERT(5, "ALERT"),
    IGNORE(3, "IGNORE"),
    FAIL_FAST(4, "FAIL_FAST")
    ;

    private final int code;
    private final String description;

    ActionTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ActionTypeEnum fromCode(int code) {
        return EnumUtils.fromCode(ActionTypeEnum.class, code);
    }
}
