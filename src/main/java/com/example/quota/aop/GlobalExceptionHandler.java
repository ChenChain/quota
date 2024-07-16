package com.example.quota.aop;

import com.example.quota.constant.ExceptionMsg;
import com.example.quota.model.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ApiResult handlerError(Exception e){
        log.error("ERROR:"+ e.getMessage());
        e.printStackTrace();
        return new ApiResult(ExceptionMsg.Internal_Error, null, ExceptionMsg.Internal_Error_Msg);
    }
}