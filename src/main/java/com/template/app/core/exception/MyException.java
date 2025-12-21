package com.template.app.core.exception;

import com.template.app.core.response.ResultCode;

public class MyException extends RuntimeException{
    private final ResultCode resultCode;
    private final int httpStatusCode;
    private final String myMessage;

    public MyException(ResultCode resultCode, int httpStatusCode) {
        this(resultCode, httpStatusCode, null);
    }

    public MyException(ResultCode resultCode, int httpStatusCode, String myMessage) {
        super(resultCode.message());
        this.resultCode = resultCode;
        this.httpStatusCode = httpStatusCode;
        this.myMessage = myMessage;
    } 

    public ResultCode getResultCode() {
        return resultCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getMyMessage() {
        return myMessage;
    }
}
