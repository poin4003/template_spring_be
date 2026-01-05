package com.template.app.core.response;

import org.springframework.http.HttpStatus;

import com.template.app.features.ops.enums.ErrorCategoryEnum;

public enum ResultCode {

    SUCCESS(0, "Success", HttpStatus.OK, ErrorCategoryEnum.SYSTEM),
    SUCCESS_CREATED(1, "Success created", HttpStatus.CREATED, ErrorCategoryEnum.SYSTEM),
    SUCCESS_NO_CONTENT(2, "Success no content", HttpStatus.NO_CONTENT, ErrorCategoryEnum.SYSTEM),

    RATE_LIMIT_ERROR(1000, "Server has reached its limit, please try again later.", HttpStatus.TOO_MANY_REQUESTS, ErrorCategoryEnum.SYSTEM),
    CIRCUIT_BREAKER_IS_OPEN(1001, "Circuit breaker is OPEN and does not permit further calls", HttpStatus.SERVICE_UNAVAILABLE, ErrorCategoryEnum.SYSTEM),

    USER_SESSION_EXPIRED(2000, "Login session is expired, please re-login", HttpStatus.UNAUTHORIZED, ErrorCategoryEnum.SECURITY),
    USER_PERMISSION_ERROR(2001, "PERMISSION DENIED", HttpStatus.FORBIDDEN, ErrorCategoryEnum.SECURITY),
    USER_AUTH_ERROR(2002, "Authentication Failed", HttpStatus.UNAUTHORIZED, ErrorCategoryEnum.SECURITY),

    RESOURCE_NOT_FOUND(4000, "Resource not found", HttpStatus.BAD_REQUEST, ErrorCategoryEnum.BUSINESS),
    PARAMS_ERROR(4001, "Invalid param", HttpStatus.BAD_REQUEST, ErrorCategoryEnum.VALIDATION),
    RESOURCE_ALREADY_EXIST(4002, "Resource already exists", HttpStatus.BAD_REQUEST, ErrorCategoryEnum.BUSINESS),
    
    ERROR(5000, "Error server occured", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCategoryEnum.SYSTEM)
    ;
    
    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
    private final ErrorCategoryEnum category;

    ResultCode(Integer code, String message, HttpStatus httpStatus, ErrorCategoryEnum category) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
        this.category = category;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    public int httpStatus() {
        return this.httpStatus.value();
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public ErrorCategoryEnum getCategory() {
        return this.category;
    }
}
