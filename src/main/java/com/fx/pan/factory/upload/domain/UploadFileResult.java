package com.fx.pan.factory.upload.domain;

import com.fx.pan.factory.constant.StorageTypeEnum;
import com.fx.pan.factory.constant.UploadFileStatusEnum;
import lombok.Data;

/**
 * @author leaving
 * @date 2022/3/4 13:01
 * @version 1.0
 */

@Data
public class UploadFileResult {
    private String fileName;
    private String extendName;
    private long fileSize;
    private String fileUrl;
    private StorageTypeEnum storageType;
    private UploadFileStatusEnum status;

}
