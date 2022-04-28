package com.fx.pan.dto.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;

/**
 * @Author leaving
 * @Date 2022/3/30 9:41
 * @Version 1.0
 */

@Data
@Schema(name = "分享文件DTO", required = true)
public class ShareFileDTO {
    @Schema(description = "文件集合")
    private ArrayList files;
    @Schema(description = "分享文件id")
    private Long fileId;
    @Schema(description = "分享文件路径")
    private String filePath;
    @Schema(description = "有效期", example = "7")
    private int expired;
    @Schema(description = "分享类型", example = "0公共分享，1私密分享，2好友分享")
    private Integer type;

}
