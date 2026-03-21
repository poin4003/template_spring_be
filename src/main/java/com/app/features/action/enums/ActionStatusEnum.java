package com.app.features.action.enums;

import com.app.core.annotation.DatabaseEnum;
import com.app.core.base.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@DatabaseEnum
public enum ActionStatusEnum implements BaseEnum {
    ACTIVE(1, "ACTIVE"),
    INACTIVE(2, "INACTIVE")
    ;
    
    private final int code;
    private final String description;
}
