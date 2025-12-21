package com.template.app.core.response;

import org.springframework.http.HttpStatus;

public enum ResultCode {

    SUCCESS(0, "Success", HttpStatus.OK),
    SUCCESS_CREATED(0, "Success created", HttpStatus.CREATED),
    SUCCESS_NO_CONTENT(0, "Success no content", HttpStatus.NO_CONTENT),

    RATE_LIMIT_ERROR(1000, "Server has reached its limit, please try again later.", HttpStatus.TOO_MANY_REQUESTS),
    CIRCUIT_BREAKER_IS_OPEN(1001, "Circuit breaker is OPEN and does not permit further calls", HttpStatus.SERVICE_UNAVAILABLE),

    USER_SESSION_EXPIRED(2000, "Login session is expired, please re-login", HttpStatus.UNAUTHORIZED),
    USER_PERMISSION_ERROR(2001, "PERMISSION DENIED", HttpStatus.FORBIDDEN),
    USER_AUTH_ERROR(2002, "Authentication Failed", HttpStatus.UNAUTHORIZED),

    RESOURCE_NOT_FOUND(4000, "Resource not found", HttpStatus.BAD_REQUEST),
    PARAMS_ERROR(4001, "Invalid param", HttpStatus.BAD_REQUEST),
    RESOURCE_ALREADY_EXIST(4002, "Resource already exists", HttpStatus.BAD_REQUEST),
    
    ERROR(5000, "Error server occured", HttpStatus.INTERNAL_SERVER_ERROR)
    ;
    
    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;

    ResultCode(Integer code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
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
}
