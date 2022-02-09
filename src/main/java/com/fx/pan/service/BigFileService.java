package com.fx.pan.service;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.Chunk;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author leaving
 * @Date 2021/12/14 9:16
 * @Version 1.0
 */
public interface BigFileService {
    Msg fileUploadPost(Chunk chunk, HttpServletResponse response);
}
