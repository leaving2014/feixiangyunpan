package com.fx.pan.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

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

    /**
     *
     */
    // private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Share other = (Share) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getBatchNum() == null ? other.getBatchNum() == null :
                this.getBatchNum().equals(other.getBatchNum()))
                && (this.getExtractionCode() == null ? other.getExtractionCode() == null :
                this.getExtractionCode().equals(other.getExtractionCode()))
                && (this.getFileId() == null ? other.getFileId() == null : this.getFileId().equals(other.getFileId()))
                && (this.getFilePath() == null ? other.getFilePath() == null :
                this.getFilePath().equals(other.getFilePath()))
                && (this.getBrowseTimes() == null ? other.getBrowseTimes() == null :
                this.getBrowseTimes().equals(other.getBrowseTimes()))
                && (this.getDownloadTimes() == null ? other.getDownloadTimes() == null :
                this.getDownloadTimes().equals(other.getDownloadTimes()))
                && (this.getSaveTimes() == null ? other.getSaveTimes() == null :
                this.getSaveTimes().equals(other.getSaveTimes()))
                && (this.getExpired() == null ? other.getExpired() == null :
                this.getExpired().equals(other.getExpired()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getShareTime() == null ? other.getShareTime() == null :
                this.getShareTime().equals(other.getShareTime()))
                && (this.getExpiredTime() == null ? other.getExpiredTime() == null :
                this.getExpiredTime().equals(other.getExpiredTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null :
                this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()));
        // && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getBatchNum() == null) ? 0 : getBatchNum().hashCode());
        result = prime * result + ((getExtractionCode() == null) ? 0 : getExtractionCode().hashCode());
        result = prime * result + ((getFileId() == null) ? 0 : getFileId().hashCode());
        result = prime * result + ((getFilePath() == null) ? 0 : getFilePath().hashCode());
        result = prime * result + ((getBrowseTimes() == null) ? 0 : getBrowseTimes().hashCode());
        result = prime * result + ((getDownloadTimes() == null) ? 0 : getDownloadTimes().hashCode());
        result = prime * result + ((getSaveTimes() == null) ? 0 : getSaveTimes().hashCode());
        result = prime * result + ((getExpired() == null) ? 0 : getExpired().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getShareTime() == null) ? 0 : getShareTime().hashCode());
        result = prime * result + ((getExpiredTime() == null) ? 0 : getExpiredTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        // result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        return result;
    }

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
