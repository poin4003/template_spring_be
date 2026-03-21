package com.app.features.action.enums;

import com.app.core.annotation.DatabaseEnum;
import com.app.core.base.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@DatabaseEnum
public enum ActionTypeEnum implements BaseEnum {
    MQ_RETRY(1, "MQ_RETRY"),
    MQ_DLQ(2, "MQ_DLQ"),
    ALERT(5, "ALERT"),
    IGNORE(3, "IGNORE"),
    FAIL_FAST(4, "FAIL_FAST")
    ;

    private final int code;
    private final String description;
}
