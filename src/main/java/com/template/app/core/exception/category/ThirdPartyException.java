package com.template.app.core.exception.category;

import com.template.app.core.exception.MyException;
import com.template.app.core.response.ResultCode;

public class ThirdPartyException extends MyException {
    public ThirdPartyException(ResultCode resultCode, int httpStatusCode, String myMessage) {
        super(resultCode, httpStatusCode, myMessage);
    } 
}
