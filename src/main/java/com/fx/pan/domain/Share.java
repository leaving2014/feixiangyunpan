package com.fx.pan.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.Getter;

/**
 * 文件分享表
 *
 * @author leaving
 * @TableName share
 */
@TableName(value = "share")
@Data
public class Share implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件共享类型(0为公共分享,1为私密分享,2为好友分享)
     */
    private Integer type;

    /**
     * 文件分享批次号
     */
    private String batchNum;

    /**
     * 文件提取码
     */
    @JSONField(serialize = false)
    private String extractionCode;

    /**
     * 文件id
     */
    private Long fileId;

    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 分享链接浏览次数
     */
    private Long browseTimes;

    /**
     * 分享文件下载次数
     */
    private Long downloadTimes;

    /**
     * 分享保存次数
     */
    private Long saveTimes;

    /**
     * 文件分享有效期,可取值1,7,30,0( 0为永久不过期)
     */
    private Integer expired;

    /**
     * 分享状态(0正常,1已失效,2已取消分享,3被冻结)
     */
    private Integer status;

    /**
     * 文件分享时间
     */
    private Date shareTime;

    /**
     * 过期时间
     */
    private Date expiredTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户id
     */
    private Long userId;

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public void setExtractionCode(String extractionCode) {
        this.extractionCode = extractionCode;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setBrowseTimes(Long browseTimes) {
        this.browseTimes = browseTimes;
    }

    public void setDownloadTimes(Long downloadTimes) {
        this.downloadTimes = downloadTimes;
    }

    public void setSaveTimes(Long saveTimes) {
        this.saveTimes = saveTimes;
    }

    public void setExpired(Integer expired) {
        this.expired = expired;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", type=").append(type);
        sb.append(", batchNum=").append(batchNum);
        sb.append(", extractionCode=").append(extractionCode);
        sb.append(", fileId=").append(fileId);
        sb.append(", filePath=").append(filePath);
        sb.append(", browseTimes=").append(browseTimes);
        sb.append(", downloadTimes=").append(downloadTimes);
        sb.append(", saveTimes=").append(saveTimes);
        sb.append(", expired=").append(expired);
        sb.append(", status=").append(status);
        sb.append(", shareTime=").append(shareTime);
        sb.append(", expiredTime=").append(expiredTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", userId=").append(userId);
        // sb.append(", deleted=").append(deleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
