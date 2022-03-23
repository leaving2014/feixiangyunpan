package com.fx.pan.dto.file;

import com.fx.pan.domain.FileBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author leaving
 * @Date 2022/2/8 16:26
 * @Version 1.0
 */
@Data
@Schema(name = "批量移动文件DTO",required = true)
public class BatchMoveFileDTO {
    @Schema(description="文件集合", required = true)
    private List<FileBean> fileList;
    @Schema(description="目的文件路径", required = true)
    private String filePath;
}
