package com.fx.pan.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author leaving
 * @Date 2022/3/10 14:35
 * @Version 1.0
 */

@Data
@Schema(name = "预览文件DTO",required = true)
public class PreviewDTO {
    private Long fileId;
    private String token;
    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description="提取码")
    private String extractionCode;
    private String isMin;
}
