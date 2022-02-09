package com.fx.pan.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author leaving
 * @Date 2021/1/18 21:21
 * @Version 1.0
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
     * 文件审核(0未审核,1审核)
     */
    private String fileAudit;
    /**
     * 文件来自(0用户上传,1文件引用,2离线下载)
     */
    private String fileOrigin;
    /**
     * 文件是否删除(逻辑删除,0未删除,1删除)
     */
    @JSONField(serialize = false)
    @TableLogic
    private String deleted;
    /**
     * 文件上传用户
     */
    private Long userId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;



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

    public String getFileIsdir() {
        return fileIsdir;
    }

    public void setFileIsdir(String fileIsdir) {
        this.fileIsdir = fileIsdir;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileShared() {
        return fileShared;
    }

    public void setFileShared(String fileShared) {
        this.fileShared = fileShared;
    }

    public Date getFileCreateTime() {
        return fileCreateTime;
    }

    public void setFileCreateTime(Date fileCreateTime) {
        this.fileCreateTime = fileCreateTime;
    }

    public Date getFileUpdateTime() {
        return fileUpdateTime;
    }

    public void setFileUpdateTime(Date fileUpdateTime) {
        this.fileUpdateTime = fileUpdateTime;
    }

    public String getFileAudit() {
        return fileAudit;
    }

    public void setFileAudit(String fileAudit) {
        this.fileAudit = fileAudit;
    }

    public String getFileOrigin() {
        return fileOrigin;
    }

    public void setFileOrigin(String fileOrigin) {
        this.fileOrigin = fileOrigin;
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

    @Override
    public String toString() {
        return "FileBean{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileIsdir='" + fileIsdir + '\'' +
                ", fileExt='" + fileExt + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", fileMd5='" + fileMd5 + '\'' +
                ", fileShared='" + fileShared + '\'' +
                ", fileCreateTime=" + fileCreateTime +
                ", fileUpdateTime=" + fileUpdateTime +
                ", fileAudit='" + fileAudit + '\'' +
                ", fileOrigin='" + fileOrigin + '\'' +
                ", deleted='" + deleted + '\'' +
                ", userId=" + userId +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    //
    // @Override
    // protected Object clone() throws CloneNotSupportedException {
    //     Object obj=super.clone();
    //     FileBean a=((FileBean)obj).getAddress();
    //     ((FileBean)obj).setAddress((FileBean) a.clone());
    //     return obj;
    // }
}
