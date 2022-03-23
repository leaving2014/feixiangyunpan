package com.fx.pan.factory.operation.download;

import com.fx.pan.factory.operation.download.domain.DownloadFile;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @Author leaving
 * @Date 2022/3/4 11:08
 * @Version 1.0
 */

public abstract class Downloader {
    public Downloader() {
    }

    public abstract void download(HttpServletResponse httpServletResponse, DownloadFile uploadFile);

    public abstract InputStream getInputStream(DownloadFile downloadFile);
}

