package com.app.core.exception.category;

import com.app.core.exception.MyException;
import com.app.core.response.ResultCode;

public class UnknownException extends MyException {
    public UnknownException(ResultCode resultCode, int httpStatusCode, String myMessage) {
        super(resultCode, httpStatusCode, myMessage);
    } 
}
