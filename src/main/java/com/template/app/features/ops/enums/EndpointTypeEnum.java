package com.template.app.features.ops.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum EndpointTypeEnum implements BaseEnum {
    MQ_BROKER_LISTENER(1, "MQ_BROKER_LISTENER"),
    API_WEBHOOK(2, "API_WEBHOOK"),
    CRONJOB(3, "CRONJOB"),
    ;

    private final int code;
    private final String description;

    EndpointTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static EndpointTypeEnum fromCode(int code) {
        return EnumUtils.fromCode(EndpointTypeEnum.class, code);
    }    
}
