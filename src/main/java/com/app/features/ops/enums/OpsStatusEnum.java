package com.app.features.ops.enums;

import com.app.core.annotation.DatabaseEnum;
import com.app.core.base.BaseEnum;

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
