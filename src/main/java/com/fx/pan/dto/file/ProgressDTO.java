package com.fx.pan.dto.file;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author leaving
 * @version 1.0
 * @date 2022/5/14 10:59
 */

@Data
public class ProgressDTO {

    /**
     * 文件id
     */
    private Long fid;
    /**
     * 操作类型
     */
    private String type;
    /**
     * 时间戳
     */
    private Long t;
    /**
     * 文件格式
     */
    private String fileExt;
    /**
     * 文件转换格式
     */
    private String convertExt;

    /**
     * 解压文件目标路径
     */
    private String filePath;
    /**
     * 解压类型
     */
    private Integer unzipMode;

    /**
     * 离线下载url
     */
    private String url;


}
