package com.fx.pan.factory.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author leaving
 * @Date 2022/3/4 12:27
 * @Version 1.0
 */

public class IOUtils {
    public static void writeInputStreamToResponse(InputStream inputStream, HttpServletResponse httpServletResponse) {
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        try {
            bis = new BufferedInputStream(inputStream);
            OutputStream os = httpServletResponse.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
