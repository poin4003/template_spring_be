package com.app.core.exception.category;

import com.app.core.exception.MyException;
import com.app.core.response.ResultCode;

public class AppSecurityException extends MyException {
    public AppSecurityException(ResultCode resultCode, int httpStatusCode, String myMessage) {
        super(resultCode, httpStatusCode, myMessage);
    }
}
