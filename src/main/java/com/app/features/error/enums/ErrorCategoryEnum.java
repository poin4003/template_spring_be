package com.app.features.error.enums;

import com.app.base.BaseEnum;
import com.app.core.annotation.DatabaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@DatabaseEnum
public enum ErrorCategoryEnum implements BaseEnum {
    INFRASTRUCTURE(1, "Infrastructure error"),
    BUSINESS(2, "Business error"),
    VALIDATION(3, "Validation error"),
    SECURITY(4, "Security error"),
    THIRD_PARTY(5, "Third party error"),
    UNKNOWN(6, "Unknown system error")
    ;

    private final int code;
    private final String description;
}
