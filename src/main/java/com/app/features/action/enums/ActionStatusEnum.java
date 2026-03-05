package com.app.features.action.enums;

import com.app.base.BaseEnum;
import com.app.core.annotation.DatabaseEnum;

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
