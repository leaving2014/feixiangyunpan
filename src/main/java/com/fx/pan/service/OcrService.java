package com.fx.pan.service;

import com.fx.pan.common.Msg;

/**
 * @Author leaving
 * @Date 2022/3/24 19:02
 * @Version 1.0
 */

public interface OcrService {
    /**
     * 文字Spring Boot
     *
     * @param readImageFile
     * @param imagePath
     * @return
     */
    Msg baiduGeneralOcr(byte[] readImageFile, String imagePath);
}
