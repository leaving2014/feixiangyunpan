package com.fx.pan.factory.operation.download.product;

import com.fx.pan.factory.operation.download.Downloader;
import com.fx.pan.factory.operation.download.domain.DownloadFile;
import com.fx.pan.factory.fxUtils;
import com.fx.pan.factory.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author leaving
 * @date 2022/3/4 11:18
 * @version 1.0
 */

@Component
public class LocalStorageDownloader extends Downloader{
    private static final Logger log = LoggerFactory.getLogger(LocalStorageDownloader.class);

    public LocalStorageDownloader() {
    }

    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        File file = new File(fxUtils.getStaticPath() + downloadFile.getFileUrl());
        if (file.exists()) {
            FileInputStream fis = null;

            try {
                fis = new FileInputStream(file);
                IOUtils.writeInputStreamToResponse(fis, httpServletResponse);
            } catch (FileNotFoundException var8) {
                log.error("File not found, file: {} ", file.getPath());
            }
        }

    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        File file = new File(fxUtils.getStaticPath() + "\\" + downloadFile.getFileUrl());
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
        }

        return inputStream;
    }

}
