package com.fx.pan.factory.exception.operation;

/**
 * @Author leaving
 * @Date 2022/3/19 16:33
 * @Version 1.0
 */

public class WriteException extends RuntimeException {
    public WriteException(Throwable cause) {
        super("文件写入出现了异常", cause);
    }

    public WriteException(String message) {
        super(message);
    }

    public WriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
