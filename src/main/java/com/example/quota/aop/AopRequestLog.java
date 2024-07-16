package com.example.quota.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static com.example.quota.constant.BaseConstant.LOG_ID;

// 打印request， response.  logID
@Slf4j
@Aspect
@Component
public class AopRequestLog {
    @Pointcut("execution(public * com.example.quota.controller..*(..))")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {
        String logId = UUID.randomUUID().toString();
        MDC.put(LOG_ID, logId);

        HttpServletRequest request = getRequest();
        if (request == null){
            return;
        }
        String params =  request.getMethod() + " "+ request.getRequestURI();
        log.info("HTTP Request: {}", params);
    }

    @AfterReturning(value = "controllerMethods()", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        log.info("Response: {}", result.toString());
        MDC.remove(LOG_ID);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (attributes == null){
            return null;
        }
        return attributes.getRequest();
    }

}
