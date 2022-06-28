package com.fx.pan.factory.operation.write.product;

import com.fx.pan.factory.FxUtils;
import com.fx.pan.factory.exception.operation.WriteException;
import com.fx.pan.factory.operation.write.Writer;
import com.fx.pan.factory.operation.write.domain.WriteFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author leaving
 * @date 2022/3/19 16:30
 * @version 1.0
 */

public class LocalStorageWriter extends Writer {
    private static final Logger log = LoggerFactory.getLogger(LocalStorageWriter.class);

    public LocalStorageWriter() {
    }

    @Override
    public void write(InputStream inputStream, WriteFile writeFile) {
        try {
            System.out.println("写文件路径-=======" + FxUtils.getStaticPath() + "/" + writeFile.getFileUrl());
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(FxUtils.getStaticPath() + "/" + writeFile.getFileUrl()),"utf-8");
            // FileOutputStream out = new FileOutputStream(FxUtils.getStaticPath() + "/" + writeFile.getFileUrl());
            // 设置编码格式为UTF-8
            Throwable var4 = null;

            try {
                byte[] bytes = new byte[1024];

                int read;
                while ((read = inputStream.read(bytes)) != -1) {
                    out.write(String.valueOf(bytes), 0, read);
                }

                out.flush();
            } catch (Throwable var16) {
                var4 = var16;
                throw var16;
            } finally {
                if (out != null) {
                    if (var4 != null) {
                        try {
                            out.close();
                        } catch (Throwable var15) {
                            var4.addSuppressed(var15);
                        }
                    } else {
                        out.close();
                    }
                }

            }
        } catch (FileNotFoundException var18) {
            throw new WriteException("待写入的文件不存在:{}", var18);
        } catch (IOException var19) {
            throw new WriteException("IO异常:{}", var19);
        }
    }
}
