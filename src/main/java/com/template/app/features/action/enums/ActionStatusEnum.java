package com.template.app.features.action.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum ActionStatusEnum implements BaseEnum {
    ACTIVE(1, "ACTIVE"),
    INACTIVE(2, "INACTIVE")
    ;
    
    private final int code;
    private final String description;

    ActionStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ActionStatusEnum fromCode(int code) {
        return EnumUtils.fromCode(ActionStatusEnum.class, code);
    }
}
