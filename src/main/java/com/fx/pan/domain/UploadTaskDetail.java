package com.fx.pan.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadTaskDetail implements Serializable {

    private Long uploadTaskDetailId;

    private String filePath;

    private String filename;

    private int chunkNumber;

    private Integer chunkSize;

    private String relativePath;

    private Integer totalChunks;

    private Integer totalSize;

    private String identifier;
}
