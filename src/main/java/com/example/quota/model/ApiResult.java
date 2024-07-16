package com.example.quota.model;


import org.slf4j.MDC;

import static com.example.quota.constant.BaseConstant.LOG_ID;

public class ApiResult {
    private int code;
    private Object data;
    private String msg;
    private String logId;

    public ApiResult() {
        this.logId = MDC.get(LOG_ID);
    }

    public ApiResult(int code, Object data) {
        this.code = code;
        this.data = data;
        this.logId = MDC.get(LOG_ID);
    }

    public ApiResult(int code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.logId = MDC.get(LOG_ID);
    }

    public static ApiResult buildSuccess(Object data){
        return new ApiResult(0, data);
    }

    public static ApiResult buildSuccess() {
        return new ApiResult(0, null);
    }

    public static ApiResult buildError(String msg, int code){
        return new ApiResult(code, "", msg);
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                ", logId='" + logId + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }
}
