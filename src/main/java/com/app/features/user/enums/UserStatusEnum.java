package com.app.features.user.enums;

import com.app.base.BaseEnum;
import com.app.core.annotation.DatabaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@DatabaseEnum
public enum UserStatusEnum implements BaseEnum {
    ACTIVE(1, "Active"),
    INACTIVE(2, "Inactive"),
    LOCKED(3, "Locked")
    ;

    private final int code;
    private final String description;
}
