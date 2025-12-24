package com.template.app.features.ops.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum MqAckStrategyEnum implements BaseEnum {
    AUTO(1, "AUTO ACK MODE"),
    MANUAL(2, "MANUAL ACK MODE"),
    ;

    private final int code;
    private final String description;

    MqAckStrategyEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static MqAckStrategyEnum fromCode(int code) {
        return EnumUtils.fromCode(MqAckStrategyEnum.class, code);
    }
}
