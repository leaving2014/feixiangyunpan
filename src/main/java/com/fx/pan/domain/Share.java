package com.fx.pan.domain;


import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

/**
 * 文件分享表(Share)表实体类
 *
 * @author makejava
 * @since 2022-01-26 23:24:22
 */
@SuppressWarnings("serial")
public class Share extends Model<Share> {
    //主键
    private Long id;
    //文件共享类型(0为私有不分享,1为公开分享,2为群组分享,3为好友分享)
    private Integer shareType;
    //文件分享链接
    private String shareLink;
    //文件提取码
    private String shareCode;
    //分享链接浏览次数
    private Integer browseTimes;
    //分享文件下载次数
    private Integer downloadTimes;
    //文件分享时间
    private Date shareTime;
    //文件分享有效期,可取值1,7,0( 0为永久不过期)
    private Date expired;
    //删除标志
    private String deleted;
    //用户id
    private Long userId;


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

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public Integer getBrowseTimes() {
        return browseTimes;
    }

    public void setBrowseTimes(Integer browseTimes) {
        this.browseTimes = browseTimes;
    }

    public Integer getDownloadTimes() {
        return downloadTimes;
    }

    public void setDownloadTimes(Integer downloadTimes) {
        this.downloadTimes = downloadTimes;
    }

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}

