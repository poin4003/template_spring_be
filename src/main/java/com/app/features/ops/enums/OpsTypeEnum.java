package com.app.features.ops.enums;

import com.app.core.annotation.DatabaseEnum;
import com.app.core.base.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@DatabaseEnum
public enum OpsTypeEnum implements BaseEnum {
    MQ_BROKER_LISTENER(1, "MQ_BROKER_LISTENER"),
    API_WEBHOOK(2, "API_WEBHOOK"),
    CRONJOB(3, "CRONJOB"),
    ;

    private final int code;
    private final String description;
}
