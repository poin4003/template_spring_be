package com.template.app.core.exception;

import com.template.app.core.response.ResultCode;

public final class ExceptionFactory {

    private ExceptionFactory() {}

    private static MyException create(ResultCode code, String messageDetail) {
        String finalMessge = (messageDetail == null || messageDetail.trim().isEmpty() ? null : messageDetail);

        return new MyException(
            code,
            code.httpStatus(),
            finalMessge
        );
    }

    public static MyException dataNotFound() {
        return create(ResultCode.RESOURCE_NOT_FOUND, null);
    }

    public static MyException dataNotFound(String messageDetail) {
        return create(ResultCode.RESOURCE_NOT_FOUND, messageDetail);
    }

    public static MyException dataAlreadyExists() {
        return create(ResultCode.RESOURCE_ALREADY_EXIST, null);
    }

    public static MyException dataAlreadyExists(String messageDetail) {
        return create(ResultCode.RESOURCE_ALREADY_EXIST, messageDetail);
    }

    public static MyException serverError() {
        return create(ResultCode.ERROR, null);
    }

    public static MyException serverError(String messageDetail) {
        return create(ResultCode.ERROR, messageDetail);
    }

    public static MyException validationError() {
        return create(ResultCode.PARAMS_ERROR, null); 
    }

    public static MyException validationError(String messageDetail) {
        return create(ResultCode.PARAMS_ERROR, messageDetail); 
    }

    public static MyException tooManyRequest() {
        return create(ResultCode.RATE_LIMIT_ERROR, null);
    }

    public static MyException tooManyRequest(String messageDetail) {
        return create(ResultCode.RATE_LIMIT_ERROR, messageDetail);
    }

    public static MyException sessionExpired() {
        return create(ResultCode.USER_SESSION_EXPIRED, null);
    }

    public static MyException sessionExpired(String messageDetail) {
        return create(ResultCode.USER_SESSION_EXPIRED, messageDetail);
    }

    public static MyException invalidTokenError() {
        return create(ResultCode.USER_AUTH_ERROR, null);
    }

    public static MyException invalidTokenError(String messageDetail) {
        return create(ResultCode.USER_AUTH_ERROR, messageDetail);
    }

    public static MyException permissionError() {
        return create(ResultCode.USER_PERMISSION_ERROR, null);
    }

    public static MyException permissionError(String messageDetail) {
        return create(ResultCode.USER_PERMISSION_ERROR, messageDetail);
    }

    public static MyException myError(ResultCode resultCode) {
        return create(resultCode, null);
    }

    public static MyException myError(ResultCode resultCode, String messageDetail) {
        return create(resultCode, messageDetail);
    }
}
