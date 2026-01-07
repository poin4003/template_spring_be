package com.template.app.config.exception;

import com.template.app.core.exception.MyException;
import com.template.app.core.response.ResultCode;
import com.template.app.core.response.ResultUtil;
import com.template.app.core.vo.ResultMessage;
import com.template.app.features.error.service.impl.ErrorMessageCacheImpl;
import com.template.app.utils.JsonUtils;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMessageCacheImpl messageCache;

    private String getBaseMessage(ResultCode resultCode) {
        String message = messageCache.getMessage(resultCode.code());
        return message != null ? message : resultCode.message();
    }

    private String mergeMessage(ResultCode resultCode, String dynamicDetail) {
        String baseMessage = getBaseMessage(resultCode);

        if (dynamicDetail == null || dynamicDetail.isBlank()) {
            return baseMessage;
        }

        if (baseMessage.endsWith(".") || baseMessage.endsWith("!") || baseMessage.endsWith("?")) {
            return baseMessage + " " + dynamicDetail;
        } else {
            return baseMessage + ". " + dynamicDetail;
        }
    }

    @ExceptionHandler(MyException.class)
    public ResponseEntity<ResultMessage<?>> handleCustomException(MyException ex) {
        ResultCode resultCode = ex.getResultCode();
        
        if (ex.getResultCode().httpStatus() >= 500) {
            log.error("[MyException] System Error: code={}, msg={}", resultCode.code(), ex.getMyMessage(), ex);
        } else {
            log.warn("[MyException] Business Error: code={}, msg={}", resultCode.code(), ex.getMyMessage());
        }

        String finalMessage = mergeMessage(resultCode, ex.getMyMessage());

        ResultMessage<?> errorMessage = ResultUtil.error(resultCode, finalMessage);
        return new ResponseEntity<>(errorMessage, Objects.requireNonNull(resultCode.getHttpStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultMessage<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + 
                    (error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"))
                .collect(Collectors.joining("; "));

        String finalMessage = mergeMessage(ResultCode.PARAMS_ERROR, errors);

        return new ResponseEntity<>(
            ResultUtil.error(ResultCode.PARAMS_ERROR, finalMessage), 
            HttpStatus.BAD_REQUEST
        );
    }
    
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResultMessage<?>> handleBindException(BindException ex) {
        log.warn("Bind exception: {}", ex.getMessage());
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
                
        String finalMessage = mergeMessage(ResultCode.PARAMS_ERROR, errors);
        
        return new ResponseEntity<>(
            ResultUtil.error(ResultCode.PARAMS_ERROR, finalMessage),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResultMessage<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Invalid request body: {}", ex.getMessage());

        String rawMessage = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        
        String errorDetail = "Invalid JSON format: " + JsonUtils.simplifyJsonErrorMessage(rawMessage);

        return new ResponseEntity<>(
            ResultUtil.error(ResultCode.PARAMS_ERROR, mergeMessage(ResultCode.PARAMS_ERROR, errorDetail)),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResultMessage<?>> handleMissingServletRequestPart(MissingServletRequestPartException ex) {
        log.warn("Missing part: {}", ex.getRequestPartName());
        String detail = "Missing form-data field: " + ex.getRequestPartName();
        return new ResponseEntity<>(
            ResultUtil.error(ResultCode.PARAMS_ERROR, mergeMessage(ResultCode.PARAMS_ERROR, detail)), 
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultMessage<?>> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        log.warn("Missing param: {}", ex.getParameterName());
        String detail = "Missing required parameter: " + ex.getParameterName();
        return new ResponseEntity<>(
            ResultUtil.error(ResultCode.PARAMS_ERROR, mergeMessage(ResultCode.PARAMS_ERROR, detail)), 
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ResultMessage<?>> handleMissingPathVariable(MissingPathVariableException ex) {
        log.warn("Missing path variable: {}", ex.getVariableName());
        String detail = "Missing path variable: " + ex.getVariableName();
        return new ResponseEntity<>(
            ResultUtil.error(ResultCode.PARAMS_ERROR, mergeMessage(ResultCode.PARAMS_ERROR, detail)), 
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ResultMessage<?>> handleRateLimitException(RequestNotPermitted ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        String msg = getBaseMessage(ResultCode.RATE_LIMIT_ERROR);
        return new ResponseEntity<>(
            ResultUtil.error(ResultCode.RATE_LIMIT_ERROR, msg), 
            HttpStatus.TOO_MANY_REQUESTS
        );
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ResultMessage<?>> handleCircuitBreakerOpen(CallNotPermittedException ex) {
        log.error("Circuit Breaker OPEN: {}", ex.getMessage());
        String msg = getBaseMessage(ResultCode.CIRCUIT_BREAKER_IS_OPEN);
        return new ResponseEntity<>(
            ResultUtil.error(ResultCode.CIRCUIT_BREAKER_IS_OPEN, msg), 
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultMessage<?>> handleAllUncaughtException(Exception ex) {
        log.error("Unknown Internal Error: ", ex);
        
        String msg = getBaseMessage(ResultCode.ERROR);
        
        return new ResponseEntity<>(
            ResultUtil.error(ResultCode.ERROR, msg),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
