package com.fx.pan.common;

/**
 * @Author leaving
 * @Date 2022/1/19 12:46
 * @Version 1.0
 */


public enum ResultCodeEnum {
    SUCCESS(true, 0, "成功"),
    UNKNOWN_ERROR(false, 20001, "未知错误"),
    PARAM_ERROR(false, 20002, "参数错误"),
    NULL_POINT(false, 20003, "空指针异常"),
    INDEX_OUT_OF_BOUNDS(false, 20004, "下标越界异常"),
    REQUEST_TIMEOUT(false, 20005, "请求超时"),
    NOT_LOGIN_ERROR(false, 20006, "未登录异常");

    private Boolean success;
    private Integer code;
    private String message;

    private ResultCodeEnum(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
