package com.fx.pan.factory.upload.domain;

import lombok.Data;

/**
 * @Author leaving
 * @Date 2022/3/4 13:01
 * @Version 1.0
 */

@Data
public class UploadFileInfo {
    private String bucketName;
    private String key;
    private String uploadId;
}
