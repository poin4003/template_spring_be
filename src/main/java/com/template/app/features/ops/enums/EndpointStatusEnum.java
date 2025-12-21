package com.template.app.features.ops.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum EndpointStatusEnum implements BaseEnum {
    ACTIVE(1, "Active"),
    DEACTIVE(2, "Deactive")
    ;

    private final int code;
    private final String description;

    EndpointStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static EndpointStatusEnum fromCode(int code) {
        return EnumUtils.fromCode(EndpointStatusEnum.class, code);
    }
}
