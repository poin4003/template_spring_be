package com.app.features.ops.enums;

import com.app.base.BaseEnum;
import com.app.core.annotation.DatabaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@DatabaseEnum
public enum OpsStatusEnum implements BaseEnum {
    ACTIVE(1, "Active"),
    DEACTIVE(2, "Deactive")
    ;

    private final int code;
    private final String description;    
}
