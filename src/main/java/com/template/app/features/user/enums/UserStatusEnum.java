package com.template.app.features.user.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum UserStatusEnum implements BaseEnum {
    ACTIVE(1, "Active"),
    INACTIVE(2, "Inactive"),
    LOCKED(3, "Locked")
    ;

    private final int code;
    private final String description;

    UserStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static UserStatusEnum fromCode(int code) {
        return EnumUtils.fromCode(UserStatusEnum.class, code);
    }
}
