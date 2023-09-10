package com.ctrip.framework.traffic.controller.utils;

/**
 * Created by jixinwang on 2023/9/6
 */
public class ApiResult<T> {

    private Integer status;

    private String message;

    private T data;

    public static <T> ApiResult getInstance( T data, Integer status, String message) {
        ApiResult<T> result = new ApiResult<T>();
        result.setData(data);
        result.setStatus(status);
        result.setMessage(message);
        return result;
    }

    public static <T> ApiResult getSuccessInstance( T data) {
        return getInstance(data, ResultCode.HANDLE_SUCCESS.getCode(), ResultCode.HANDLE_SUCCESS.getMessage());
    }

    public static <T> ApiResult getSuccessInstance( T data,String message) {
        return getInstance(data, ResultCode.HANDLE_SUCCESS.getCode(), message);
    }

    public static <T> ApiResult getFailInstance( T data) {
        return getInstance(data, ResultCode.HANDLE_FAIL.getCode(), ResultCode.HANDLE_FAIL.getMessage());
    }

    public static <T> ApiResult getFailInstance( T data,String message) {
        return getInstance(data, ResultCode.HANDLE_FAIL.getCode(), message);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
