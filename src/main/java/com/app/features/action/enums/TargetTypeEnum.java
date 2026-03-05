package com.app.features.action.enums;

import com.app.base.BaseEnum;
import com.app.core.annotation.DatabaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@DatabaseEnum
public enum TargetTypeEnum implements BaseEnum {
    GLOBAL(0, "GLOBAL"),
    MQ_CONSUMER(1, "MQ_CONSUMER"),
    WEBHOOK(2, "WEBHOOK"),
    API(3, "API")
    ;
    
    private final int code;
    private final String description;
}
