package com.fx.pan.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;

/**
 * @Author leaving
 * @Date 2022/4/1 19:46
 * @Version 1.0
 */
@Data
public class ShareFileSaveDTO {
    @Schema(description = "批次号")
    private String batchNum;
    @Schema(description = "文件列表")
    private Long[] files;
    @Schema(description = "保存路径")
    private String filePath;
}
