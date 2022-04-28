package com.fx.pan.service;

import com.fx.pan.common.Msg;

/**
 * @Author leaving
 * @Date 2022/3/28 10:31
 * @Version 1.0
 */
public interface OfflineDownloadService {
    Msg downloadFromUrl(String url);
}
