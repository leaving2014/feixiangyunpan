package com.fx.pan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author leaving
 * @date 2021/1/18 21:21
 * @version 1.0
 * @TableName file
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("file")
public class FileBean implements Serializable,Cloneable{
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
     * 文件是否删除(逻辑删除,0:未删除,1:删除)
     */
    private Integer deleted;
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
     * 文件审核(-1:未审核,0:审核中,1:审核通过)
     */
    private Integer audit;
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public Integer getIsDir() {
        return isDir;
    }

    public void setIsDir(Integer isDir) {
        this.isDir = isDir;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Integer getIsShared() {
        return isShared;
    }

    public void setIsShared(Integer isShared) {
        this.isShared = isShared;
    }

    public Integer getStorageType() {
        return storageType;
    }

    public void setStorageType(Integer storageType) {
        this.storageType = storageType;
    }

    public Integer getAudit() {
        return audit;
    }

    public void setAudit(Integer audit) {
        this.audit = audit;
    }

    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public Long getParentPathId() {
        return parentPathId;
    }

    public void setParentPathId(Long parentPathId) {
        this.parentPathId = parentPathId;
    }

    public Date getFileUpdateTime() {
        return fileUpdateTime;
    }

    public void setFileUpdateTime(Date fileUpdateTime) {
        this.fileUpdateTime = fileUpdateTime;
    }

    public Date getFileCreateTime() {
        return fileCreateTime;
    }

    public void setFileCreateTime(Date fileCreateTime) {
        this.fileCreateTime = fileCreateTime;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileExt='" + fileExt + '\'' +
                ", fileSize=" + fileSize +
                ", fileType=" + fileType +
                ", deleted=" + deleted +
                ", userId=" + userId +
                ", originName='" + originName + '\'' +
                ", isDir=" + isDir +
                ", identifier='" + identifier + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", isShared=" + isShared +
                ", storageType=" + storageType +
                ", audit=" + audit +
                ", origin=" + origin +
                ", parentPathId=" + parentPathId +
                ", fileUpdateTime=" + fileUpdateTime +
                ", fileCreateTime=" + fileCreateTime +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
