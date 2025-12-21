package com.template.app.features.sims.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum SimStatusEnum implements BaseEnum {
    ACTIVE(1, "Active"),
    INACTIVE(2, "Inactive"),
    BLOCKED(3, "Blocked"),
    PICKED(4, "Picked"),
    DELETED(5, "Deleted");

    private final int code;
    private final String description;

    SimStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static SimStatusEnum fromCode(int code) {
        return EnumUtils.fromCode(SimStatusEnum.class, code);
    }
}
