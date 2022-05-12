package com.fx.pan.service;

import com.fx.pan.domain.ResponseResult;

/**
 * @author leaving
 * @date 2022/5/8 19:58
 * @version 1.0
 */

public interface BaiduOcrService {
    ResponseResult baiduGeneralOcr(byte[] bytesFile, String imagePath);
}
