package com.template.app.config.exception;

import com.template.app.core.exception.MyException;
import com.template.app.core.response.ResultCode;
import com.template.app.core.response.ResultUtil;
import com.template.app.core.vo.ResultMessage;
import com.template.app.utils.JsonUtils;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MyException.class)
    public ResponseEntity<ResultMessage<?>> handleCustomException(MyException ex) {
        log.error("MyException: {}, code: {}", ex.getMyMessage(), ex.getResultCode(), ex);

        HttpStatus httpStatus = ex.getResultCode().getHttpStatus();
        
        String finalMessage = (ex.getMyMessage() != null && !ex.getMyMessage().trim().isEmpty()) 
                                 ? ex.getMyMessage() 
                                 : ex.getResultCode().message();
                                     
        ResultMessage<?> errorMessage = ResultUtil.error(ex.getResultCode(), finalMessage);
        
        return new ResponseEntity<>(errorMessage, Objects.requireNonNull(httpStatus));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultMessage<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error caught: {}", ex.getMessage(), ex);

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + 
                    (error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"))
                .collect(Collectors.joining("; "));

        ResultMessage<?> errorMessage = ResultUtil.error(
                ResultCode.PARAMS_ERROR,
                ResultCode.PARAMS_ERROR.message() + ": " + errors
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResultMessage<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Invalid request body: {}", ex.getMessage(), ex);

        String rawMessage = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        String errorDetail = JsonUtils.simplifyJsonErrorMessage(rawMessage);

        ResultMessage<?> errorMessage = ResultUtil.error(
                ResultCode.PARAMS_ERROR,
                "Invalid request body: " + errorDetail
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResultMessage<?>> handleMissingServletRequestPart(MissingServletRequestPartException ex) {
        log.warn("Missing form-data part: {}", ex.getMessage(), ex);
        ResultMessage<?> errorMessage = ResultUtil.error(
                ResultCode.PARAMS_ERROR,
                "Missing form-data field: " + ex.getRequestPartName()
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultMessage<?>> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getMessage(), ex);
        ResultMessage<?> errorMessage = ResultUtil.error(
                ResultCode.PARAMS_ERROR,
                "Missing required parameter: " + ex.getParameterName()
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ResultMessage<?>> handleMissingPathVariable(MissingPathVariableException ex) {
        log.warn("Missing path variable: {}", ex.getMessage(), ex);
        ResultMessage<?> errorMessage = ResultUtil.error(
                ResultCode.PARAMS_ERROR,
                "Missing path variable: " + ex.getVariableName()
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ResultMessage<?>> handleRateLimitException(RequestNotPermitted ex) {
        log.warn("Validation error: {}", ex.getMessage(), ex);
        ResultMessage<?> errorMessage = ResultUtil.error(
            ResultCode.RATE_LIMIT_ERROR
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ResultMessage<?>> handleCircuitBreakerOpen(CallNotPermittedException ex) {
        log.warn("Validation error: {}", ex.getMessage(), ex);
        ResultMessage<?> errorMessage = ResultUtil.error(
            ResultCode.CIRCUIT_BREAKER_IS_OPEN
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.SERVICE_UNAVAILABLE);
    } 
}
