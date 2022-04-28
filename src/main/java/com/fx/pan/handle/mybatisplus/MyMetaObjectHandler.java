package com.fx.pan.handle.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.fx.pan.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @Author leaving
 * @Date 2022/2/11 16:11
 * @Version 1.0
 */

public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = null;

        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            userId = -1L; // 表示是自己创建
        }

        boolean hasSetter = metaObject.hasSetter("fileCreateTime");
        // boolean hasSetter = metaObject.hasSetter("createTime");
        if (metaObject.hasSetter("fileCreateTime")) {
            this.setFieldValByName("fileCreateTime", new Date(), metaObject);
            this.setFieldValByName("fileUpdateTime", new Date(), metaObject);
        }
        if (metaObject.hasSetter("createTime")) {
            this.setFieldValByName("createTime", new Date(), metaObject);
            this.setFieldValByName("updateTime", new Date(), metaObject);

        }

        // this.setFieldValByName("updateBy", userId, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("fileUpdateTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("userId", SecurityUtils.getUserId(), metaObject);
    }
}
