package com.fx.pan.factory.constant;

/**
 * @author leaving
 * @date 2022/3/4 13:07
 * @version 1.0
 */

public enum UploadFileStatusEnum {

    FAIL(0, "上传失败"),
    SUCCESS(1, "上传成功"),
    UNCOMPLATE(3, "未完成");

    private int code;
    private String message;

    UploadFileStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
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
