package com.fx.pan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author leaving
 * @date 2022/4/7 17:33
 * @version 1.0
 */

@Data
public class UploadTask implements Serializable {

    private Long uploadTaskId;

    // 用户id
    private Long userId;

    // md5唯一标识
    private String identifier;

    // 文件名称
    private String fileName;

    // 文件路径
    private String filePath;

    //扩展名
    private String extendName;

    // 上传时间
    private String uploadTime;

    // 上传状态(1-成功,0-失败或未完成)
    private Integer uploadStatus;
}
