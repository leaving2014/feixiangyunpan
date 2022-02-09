package com.fx.pan.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author leaving
 * @Date 2022/2/9 17:13
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileListVo {
    private Long id;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 是否为目录
     */
    private String fileIsdir;
    /**
     * 文件扩展名
     */
    private String fileExt;
    /**
     * 文件大小(单位B)
     */
    private Long fileSize;
    /**
     * 文件类型(0未知,1图片,2文档,3视频,4音频)
     */
    private String fileType;
    /**
     * 文件md5(用于快速上传)
     */
    private String fileMd5;
    /**
     * 文件是否共享(0不共享,1共享)
     */
    private String fileShared;
    /**
     * 文件创建时间
     */
    private Date fileCreateTime;
    /**
     * 文件更新时间
     */
    private Date fileUpdateTime;
    /**
     * 文件来自(0用户上传,1文件引用,2离线下载)
     */
    private String fileOrigin;

}
