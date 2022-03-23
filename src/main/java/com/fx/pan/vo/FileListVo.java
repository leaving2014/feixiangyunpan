package com.fx.pan.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
     * 文件扩展名
     */
    private String fileExt;
    /**
     * 文件大小(单位B)
     */
    private Long fileSize;
    /**
     * 文件类型(0未知,1图片,2文档,3视频,4种子,5音频,6其他)
     */
    private Integer fileType;
    /**
     * 文件上传用户id
     */
    private Long userId;
    /**
     * 文件原始名称(一般不会改变,对应磁盘文件名)
     */
    private String originName;
    /**
     * 是否为目录
     */
    private Integer isDir;
    /**
     * 文件md5(用于快速上传)
     */
    private String identifier;
    /**
     * 文件url
     */
    private String fileUrl;
    /**
     * 文件是否共享(0不共享,1共享)
     */
    private Integer isShared;
    /**
     * 文件存储类型 (0:本地存储 1:cos对象存储)
     */
    private Integer storageType;
    /**
     * 文件来源(0: 用户上传,1: 文件引用(用户保存的分享文件),2: 离线下载)
     */
    private Integer origin;
    /**
     * 文件的父目录id (根目录下的目录父目录和文件为-1)
     */
    private Long parentPathId;
    /**
     * 文件更新时间
     */
    private Date fileUpdateTime;
    /**
     * 文件创建时间
     */
    private Date fileCreateTime;

}
