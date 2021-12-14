package com.fx.pan.interfaces;

import com.fx.pan.entity.Chunk;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author leaving
 * @Date 2021/12/14 9:16
 * @Version 1.0
 */
public interface BigFileServiceInterface {
    String fileUploadPost(Chunk chunk, HttpServletResponse response);
}
