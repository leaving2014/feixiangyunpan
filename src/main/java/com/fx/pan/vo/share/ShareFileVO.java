package com.fx.pan.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author leaving
 * @Date 2022/3/30 14:31
 * @Version 1.0
 */

@Data
@Schema(description = "分享文件VO")
public class ShareFileVO {
    @Schema(description = "批次号")
    private String batchNum;
    @Schema(description = "提取编码")
    private String extractionCode;
}
