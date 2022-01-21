package com.fx.pan.exception;

/**
 * @Author leaving
 * @Date 2022/1/19 14:42
 * @Version 1.0
 */

public class UploadException extends RuntimeException {
    public UploadException(Throwable cause) {
        super("上传出现了异常", cause);
    }

    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
