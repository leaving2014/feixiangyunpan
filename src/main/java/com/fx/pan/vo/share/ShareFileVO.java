package com.fx.pan.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author leaving
 * @date 2022/3/30 14:31
 * @version 1.0
 */

@Data
@Schema(description = "分享文件VO")
public class ShareFileVO {
    private Long id;
    private String fileName;
    private String filePath;
    private String fileType;
    private String fileSize;
    private String fileCreateTime;
    private String fileUpdateTime;
    private String userId;
    private String identifier;
    private Boolean isDir;
    private Integer storageType;
}
