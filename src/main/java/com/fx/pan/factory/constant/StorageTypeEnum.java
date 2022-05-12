package com.fx.pan.factory.constant;

/**
 * @author leaving
 * @date 2022/3/4 11:12
 * @version 1.0
 */
public enum StorageTypeEnum {
    LOCAL(0, "本地存储"),
    COS(1,"腾讯COS对象存储");

    private int code;
    private String name;

    private StorageTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
