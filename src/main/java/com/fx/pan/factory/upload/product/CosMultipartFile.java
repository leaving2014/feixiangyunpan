package com.fx.pan.factory.upload.product;

import com.fx.pan.factory.fxUtils;
import com.fx.pan.utils.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author leaving
 * @date 2022/3/4 13:03
 * @version 1.0
 */

public class CosMultipartFile {

    MultipartFile multipartFile = null;

    public CosMultipartFile() {
    }

    public CosMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public String getFileName() {

        String originalName = getMultipartFile().getOriginalFilename();
        if (!originalName.contains(".")) {
            return originalName;
        }
        return originalName.substring(0, originalName.lastIndexOf("."));
    }

    public String getExtendName() {
        String originalName = getMultipartFile().getOriginalFilename();
        String extendName = FileUtils.getFileExtendName(originalName);
        return extendName;
    }

    public String getFileUrl() {
        String uuid = UUID.randomUUID().toString();
        String fileUrl = fxUtils.getUploadFileUrl(uuid, getExtendName());
        return fileUrl;
    }

    public String getFileUrl(String identify) {
        String fileUrl = fxUtils.getUploadFileUrl(identify, getExtendName());
        return fileUrl;
    }

    public InputStream getUploadInputStream() throws IOException {
        return getMultipartFile().getInputStream();
    }

    public byte[] getUploadBytes() throws IOException {
        return getMultipartFile().getBytes();
    }

    public long getSize() {
        long size = getMultipartFile().getSize();
        return size;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    private void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
