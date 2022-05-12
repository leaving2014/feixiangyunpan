package com.fx.pan.service;

import com.fx.pan.domain.ResponseResult;
import org.springframework.scheduling.annotation.Async;

import java.io.FileNotFoundException;

/**
 * @author leaving
 * @date 2022/3/28 10:31
 * @version 1.0
 */

public interface OfflineDownloadService {

    ResponseResult downloadFromUrl(String url,Long t,Long userId,String type) throws FileNotFoundException;
}
