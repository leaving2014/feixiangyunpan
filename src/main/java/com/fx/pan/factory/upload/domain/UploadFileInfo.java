package com.fx.pan.factory.upload.domain;

import lombok.Data;

/**
 * @author leaving
 * @date 2022/3/4 13:01
 * @version 1.0
 */

@Data
public class UploadFileInfo {
    private String bucketName;
    private String key;
    private String uploadId;
}
