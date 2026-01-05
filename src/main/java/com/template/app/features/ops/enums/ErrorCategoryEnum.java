package com.template.app.features.ops.enums;

import com.template.app.base.BaseEnum;
import com.template.app.utils.EnumUtils;

import lombok.Getter;

@Getter
public enum ErrorCategoryEnum implements BaseEnum {
    SYSTEM(1, "Infrastructure error"),
    BUSINESS(2, "Business error"),
    VALIDATION(3, "Validation error"),
    SECURITY(4, "Security error"),
    THIRD_PARTY(5, "Third party error")
    ;
    private final int code;
    private final String description;

    ErrorCategoryEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ErrorCategoryEnum fromCode(int code) {
        return EnumUtils.fromCode(ErrorCategoryEnum.class, code);
    }
}
