package com.template.app.core.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;

import com.template.app.core.response.ResultCode;
import com.template.app.core.response.ResultUtil;
import com.template.app.core.vo.ResultMessage;

public abstract class BaseController {
    private <T> ResponseEntity<ResultMessage<T>> create(
        ResultCode resultCode,
        String myMessage,
        T data
    ) {
        ResultMessage<T> message = ResultUtil.success(resultCode, myMessage, data);
        
        return new ResponseEntity<>(message, Objects.requireNonNull(resultCode.getHttpStatus()));
    }

    protected <T> ResponseEntity<ResultMessage<T>> OK(T data) {
        return create(ResultCode.SUCCESS, null, data);
    }

    protected <T> ResponseEntity<ResultMessage<T>> OK(String myMessage, T data) {
        return create(ResultCode.SUCCESS, myMessage, data);
    }

    protected <T> ResponseEntity<ResultMessage<T>> Created(T data) {
        return create(ResultCode.SUCCESS_CREATED, null, data);
    }

    protected <T> ResponseEntity<ResultMessage<T>> Created(String myMessage, T data) {
        return create(ResultCode.SUCCESS_CREATED, myMessage, data);
    }

    protected <T> ResponseEntity<ResultMessage<T>> NoContent() {
        return create(ResultCode.SUCCESS_NO_CONTENT, null, null);
    }

    protected <T> ResponseEntity<ResultMessage<T>> NoContent(String myMessage) {
        return create(ResultCode.SUCCESS_NO_CONTENT, myMessage, null);
    }
}
