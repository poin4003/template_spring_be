package com.template.app.core.exception.category;

import com.template.app.core.exception.MyException;
import com.template.app.core.response.ResultCode;

public class InfrastructureException extends MyException {
    public InfrastructureException(ResultCode resultCode, int httpStatusCode, String myMessage) {
        super(resultCode, httpStatusCode, myMessage);
    }
}
