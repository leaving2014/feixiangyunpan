package com.fx.pan.exception;

/**
 * @author leaving
 * @date 2022/1/19 14:40
 * @version 1.0
 */

public class CopyException extends RuntimeException {
    public CopyException(Throwable cause) {
        super("创建出现了异常", cause);
    }

    public CopyException(String message) {
        super(message);
    }

    public CopyException(String message, Throwable cause) {
        super(message, cause);
    }
}
