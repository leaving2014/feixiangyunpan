package com.fx.pan.dto.file;

import com.fx.pan.domain.FileBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author leaving
 * @date 2022/3/11 15:04
 * @version 1.0
 */
@Data
@Schema(name = "批量复制文件DTO",required = true)
public class BatchCopyFileDTO {
    @Schema(description="文件集合", required = true)
    private List<FileBean> fileList;
    @Schema(description="目的文件路径", required = true)
    private String filePath;
}
