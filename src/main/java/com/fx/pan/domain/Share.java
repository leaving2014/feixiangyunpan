package com.fx.pan.domain;


import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件分享表(Share)表实体类
 *
 * @author leaving
 * @since 2022-01-26 23:24:22
 */
@SuppressWarnings("serial")
public class Share extends Model<Share> implements Serializable {
    //主键
    private Long id;
    //文件共享类型(0为公共分享,1为私密分享,2为好友分享)
    private Integer shareType;
    //文件分享链接
    private String shareLink;
    //分享链接浏览次数
    private Long browseTimes;
    //分享文件下载次数
    private Long downloadTimes;
    //文件分享时间
    private Date shareTime;
    //文件分享有效期,可取值1,7,30,0( 0为永久不过期)
    private Integer expired;
    //用户id
    private Long userId;
    //文件提取码
    private String extractionCode;
    //更新时间
    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getShareType() {
        return shareType;
    }

    public void setShareType(Integer shareType) {
        this.shareType = shareType;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public Long getBrowseTimes() {
        return browseTimes;
    }

    public void setBrowseTimes(Long browseTimes) {
        this.browseTimes = browseTimes;
    }

    public Long getDownloadTimes() {
        return downloadTimes;
    }

    public void setDownloadTimes(Long downloadTimes) {
        this.downloadTimes = downloadTimes;
    }

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public Integer getExpired() {
        return expired;
    }

    public void setExpired(Integer expired) {
        this.expired = expired;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getExtractionCode() {
        return extractionCode;
    }

    public void setExtractionCode(String extractionCode) {
        this.extractionCode = extractionCode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Share{" +
                "id=" + id +
                ", shareType=" + shareType +
                ", shareLink='" + shareLink + '\'' +
                ", browseTimes=" + browseTimes +
                ", downloadTimes=" + downloadTimes +
                ", shareTime=" + shareTime +
                ", expired=" + expired +
                ", userId=" + userId +
                ", extractionCode='" + extractionCode + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }

}

