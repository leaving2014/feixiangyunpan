package com.fx.pan.vo.share;

import com.fx.pan.vo.UserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.bouncycastle.util.Times;

import java.util.Date;

/**
 * @author leaving
 * @date 2022/3/31 15:41
 * @version 1.0
 */
@Data
@Schema(description = "分享文件列表VO")
public class ShareFileListVO {
    private Long id;
    private Long fileId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private Date fileCreateTime;
    private Integer fileType;
    private Integer isDir;
    private Integer storageType;
    private Long parentPathId;
    private String fileExt;
    private String identifier;
    private Long userId;

    public String batchNum;
    private String extractionCode;
    private Integer type;
    private Long saveTimes;

    private Long downloadTimes;
    private Long browseTimes;

    private Integer status;
    private Date shareTime;
    private Date expiredTime;
    private Date updateTime;
    private Integer expired;

    public UserVo user;

}
