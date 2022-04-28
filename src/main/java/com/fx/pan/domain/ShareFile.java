package com.fx.pan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author leaving
 * @Date 2022/3/30 14:04
 * @Version 1.0
 */

@Data

@TableName("share_file")
public class ShareFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 分享批次号
     */
    private String batchNum;
    /**
     * 文件id
     */
    private Long fileId;
    /**
     * 分享文件路径
     */
    private String shareFilePath;

}
