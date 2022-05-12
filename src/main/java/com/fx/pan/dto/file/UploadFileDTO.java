package com.fx.pan.dto.file;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @author leaving
 * @date 2022/1/23 22:43
 * @version 1.0
 */

@Data
@Schema(name = "上传文件DTO",required = true)
public class UploadFileDTO {

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "切片数量")
    private int chunkNumber;

    @Schema(description = "切片大小")
    private long chunkSize;
    @Schema(description = "相对路径")
    private String relativePath;

    @Schema(description = "所有切片")
    private int totalChunks;
    @Schema(description = "总大小")
    private long totalSize;
    @Schema(description = "当前切片大小")
    private long currentChunkSize;
    @Schema(description = "md5码")
    private String identifier;
}
