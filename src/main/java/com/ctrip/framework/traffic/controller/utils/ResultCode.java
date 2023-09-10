package com.ctrip.framework.traffic.controller.utils;

/**
 * Created by jixinwang on 2023/9/6
 */
public enum ResultCode {

    HANDLE_SUCCESS(0, "handle success"),

    HANDLE_FAIL(1, "handle fail"),

    UNKNOWN_ERROR(100, "unknown error");

    private int code;

    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public static ResultCode getResultCode(int error) {
        for (ResultCode resultCode : values()) {
            if (resultCode.getCode() == error) {
                return resultCode;
            }
        }

        return UNKNOWN_ERROR;
    }

    public static ResultCode getUnknownError(String error) {
        UNKNOWN_ERROR.setMessage(error);
        return UNKNOWN_ERROR;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
