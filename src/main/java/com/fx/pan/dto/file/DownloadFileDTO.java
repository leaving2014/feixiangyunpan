package com.fx.pan.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "下载文件DTO",required = true)
public class DownloadFileDTO {
    private long userFileId;
    private String fileName;
    private String token;
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description="提取码")
    private String extractionCode;
}
