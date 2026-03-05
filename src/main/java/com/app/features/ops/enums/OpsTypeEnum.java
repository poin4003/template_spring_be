package com.app.features.ops.enums;

import com.app.base.BaseEnum;
import com.app.core.annotation.DatabaseEnum;

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
