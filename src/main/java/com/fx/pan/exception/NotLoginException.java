package com.fx.pan.exception;

/**
 * @author leaving
 * @date 2022/1/19 14:38
 * @version 1.0
 */

public class NotLoginException extends RuntimeException{
    public NotLoginException() {
        super("未登录");
    }
    public NotLoginException(Throwable cause) {
        super("未登录", cause);
    }

    public NotLoginException(String message) {
        super(message);
    }

    public NotLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}

