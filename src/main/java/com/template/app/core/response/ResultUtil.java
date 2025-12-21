package com.template.app.core.response;

import com.template.app.core.vo.ResultMessage;

public final class ResultUtil {
    private ResultUtil() {}

    private static <T> ResultMessage<T> create(ResultCode resultCode, String myMessage, T data) {
        ResultMessage<T> responseMessage = new ResultMessage<>();

        String finalMessage = (myMessage != null && !myMessage.isEmpty()
                            ? myMessage
                            : resultCode.message());

        responseMessage.setSuccess(resultCode.code().equals(0));
        responseMessage.setMessage(finalMessage);
        responseMessage.setCode(resultCode.code());
        responseMessage.setResult(data);

        return responseMessage;
    }

    public static <T> ResultMessage<T> success(T data) {
        return create(ResultCode.SUCCESS, null, data);
    }

    public static <T> ResultMessage<T> success(ResultCode resultCode, String myMessage, T data) {
        return create(resultCode, myMessage, data);
    }

    public static <T> ResultMessage<T> error(ResultCode resultCode, String myMessage) {
        return create(resultCode, myMessage, null);
    }

    public static <T> ResultMessage<T> error(ResultCode resultCode) {
        return create(resultCode, null, null);
    }    
}
