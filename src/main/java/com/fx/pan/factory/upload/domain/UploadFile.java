package com.fx.pan.factory.upload.domain;

import lombok.Data;

/**
 * @author leaving
 * @date 2022/3/4 13:01
 * @version 1.0
 */

@Data
public class UploadFile {

    //切片上传相关参数
    private int chunkNumber;
    private long chunkSize;
    private int totalChunks;
    private String identifier;
    private long totalSize;
    private long currentChunkSize;


}
