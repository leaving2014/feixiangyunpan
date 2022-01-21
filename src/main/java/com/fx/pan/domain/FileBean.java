package com.fx.pan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
/**
 * @Author leaving
 * @Date 2021/1/18 21:21
 * @Version 1.0
 * @TableName file
 */

@TableName("file")
public class FileBean implements Serializable {
    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件存储位置,0为本地,1为cos对象存储
     */
    private String fileStorageType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件绝对路径
     */
    private String filePathAbs;

    /**
     * 是否为目录
     */
    private Integer fileIsdir;

    /**
     * 文件扩展名
     */
    private String fileExt;

    /**
     * 文件大小
     */
    private Double fileSize;

    /**
     * 文件类型,0未知,1图片,2文档,3视频,4音频
     */
    private String fileCategory;

    /**
     * 文件md5,用于快速上传
     */
    private String fileMd5;

    /**
     * 文件共享类型,0为私有不分享,1为公开分享,2为群组分享
     */
    private Integer fileSharedType;

    /**
     * 文件是否收藏
     */
    private String fileCollected;

    /**
     * 文件创建时间
     */
    private Date fileCreateTime;

    /**
     * 文件更新时间
     */
    private Date fileUpdateTime;

    /**
     * 是否删除(0未删除,1已删除)
     */
    private String delete;

    /**
     * 文件上传用户
     */
    private Long userId;

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public Long getFileId() {
        return fileId;
    }

    /**
     * 
     */
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    /**
     * 文件名称
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 文件名称
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 文件存储位置,0为本地,1为cos对象存储
     */
    public String getFileStorageType() {
        return fileStorageType;
    }

    /**
     * 文件存储位置,0为本地,1为cos对象存储
     */
    public void setFileStorageType(String fileStorageType) {
        this.fileStorageType = fileStorageType;
    }

    /**
     * 文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 文件路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 文件绝对路径
     */
    public String getFilePathAbs() {
        return filePathAbs;
    }

    /**
     * 文件绝对路径
     */
    public void setFilePathAbs(String filePathAbs) {
        this.filePathAbs = filePathAbs;
    }

    /**
     * 是否为目录
     */
    public Integer getFileIsdir() {
        return fileIsdir;
    }

    /**
     * 是否为目录
     */
    public void setFileIsdir(Integer fileIsdir) {
        this.fileIsdir = fileIsdir;
    }

    /**
     * 文件扩展名
     */
    public String getFileExt() {
        return fileExt;
    }

    /**
     * 文件扩展名
     */
    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    /**
     * 文件大小
     */
    public Double getFileSize() {
        return fileSize;
    }

    /**
     * 文件大小
     */
    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 文件类型,0未知,1图片,2文档,3视频,4音频
     */
    public String getFileCategory() {
        return fileCategory;
    }

    /**
     * 文件类型,0未知,1图片,2文档,3视频,4音频
     */
    public void setFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
    }

    /**
     * 文件md5,用于快速上传
     */
    public String getFileMd5() {
        return fileMd5;
    }

    /**
     * 文件md5,用于快速上传
     */
    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    /**
     * 文件共享类型,0为私有不分享,1为公开分享,2为群组分享
     */
    public Integer getFileSharedType() {
        return fileSharedType;
    }

    /**
     * 文件共享类型,0为私有不分享,1为公开分享,2为群组分享
     */
    public void setFileSharedType(Integer fileSharedType) {
        this.fileSharedType = fileSharedType;
    }

    /**
     * 文件是否收藏
     */
    public String getFileCollected() {
        return fileCollected;
    }

    /**
     * 文件是否收藏
     */
    public void setFileCollected(String fileCollected) {
        this.fileCollected = fileCollected;
    }

    /**
     * 文件创建时间
     */
    public Date getFileCreateTime() {
        return fileCreateTime;
    }

    /**
     * 文件创建时间
     */
    public void setFileCreateTime(Date fileCreateTime) {
        this.fileCreateTime = fileCreateTime;
    }

    /**
     * 文件更新时间
     */
    public Date getFileUpdateTime() {
        return fileUpdateTime;
    }

    /**
     * 文件更新时间
     */
    public void setFileUpdateTime(Date fileUpdateTime) {
        this.fileUpdateTime = fileUpdateTime;
    }

    /**
     * 是否删除(0未删除,1已删除)
     */
    public String getDelete() {
        return delete;
    }

    /**
     * 是否删除(0未删除,1已删除)
     */
    public void setDelete(String delete) {
        this.delete = delete;
    }

    /**
     * 文件上传用户
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 文件上传用户
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
        FileBean other = (FileBean) that;
        return (this.getFileId() == null ? other.getFileId() == null : this.getFileId().equals(other.getFileId()))
            && (this.getFileName() == null ? other.getFileName() == null : this.getFileName().equals(other.getFileName()))
            && (this.getFileStorageType() == null ? other.getFileStorageType() == null : this.getFileStorageType().equals(other.getFileStorageType()))
            && (this.getFilePath() == null ? other.getFilePath() == null : this.getFilePath().equals(other.getFilePath()))
            && (this.getFilePathAbs() == null ? other.getFilePathAbs() == null : this.getFilePathAbs().equals(other.getFilePathAbs()))
            && (this.getFileIsdir() == null ? other.getFileIsdir() == null : this.getFileIsdir().equals(other.getFileIsdir()))
            && (this.getFileExt() == null ? other.getFileExt() == null : this.getFileExt().equals(other.getFileExt()))
            && (this.getFileSize() == null ? other.getFileSize() == null : this.getFileSize().equals(other.getFileSize()))
            && (this.getFileCategory() == null ? other.getFileCategory() == null : this.getFileCategory().equals(other.getFileCategory()))
            && (this.getFileMd5() == null ? other.getFileMd5() == null : this.getFileMd5().equals(other.getFileMd5()))
            && (this.getFileSharedType() == null ? other.getFileSharedType() == null : this.getFileSharedType().equals(other.getFileSharedType()))
            && (this.getFileCollected() == null ? other.getFileCollected() == null : this.getFileCollected().equals(other.getFileCollected()))
            && (this.getFileCreateTime() == null ? other.getFileCreateTime() == null : this.getFileCreateTime().equals(other.getFileCreateTime()))
            && (this.getFileUpdateTime() == null ? other.getFileUpdateTime() == null : this.getFileUpdateTime().equals(other.getFileUpdateTime()))
            && (this.getDelete() == null ? other.getDelete() == null : this.getDelete().equals(other.getDelete()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFileId() == null) ? 0 : getFileId().hashCode());
        result = prime * result + ((getFileName() == null) ? 0 : getFileName().hashCode());
        result = prime * result + ((getFileStorageType() == null) ? 0 : getFileStorageType().hashCode());
        result = prime * result + ((getFilePath() == null) ? 0 : getFilePath().hashCode());
        result = prime * result + ((getFilePathAbs() == null) ? 0 : getFilePathAbs().hashCode());
        result = prime * result + ((getFileIsdir() == null) ? 0 : getFileIsdir().hashCode());
        result = prime * result + ((getFileExt() == null) ? 0 : getFileExt().hashCode());
        result = prime * result + ((getFileSize() == null) ? 0 : getFileSize().hashCode());
        result = prime * result + ((getFileCategory() == null) ? 0 : getFileCategory().hashCode());
        result = prime * result + ((getFileMd5() == null) ? 0 : getFileMd5().hashCode());
        result = prime * result + ((getFileSharedType() == null) ? 0 : getFileSharedType().hashCode());
        result = prime * result + ((getFileCollected() == null) ? 0 : getFileCollected().hashCode());
        result = prime * result + ((getFileCreateTime() == null) ? 0 : getFileCreateTime().hashCode());
        result = prime * result + ((getFileUpdateTime() == null) ? 0 : getFileUpdateTime().hashCode());
        result = prime * result + ((getDelete() == null) ? 0 : getDelete().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", fileId=").append(fileId);
        sb.append(", fileName=").append(fileName);
        sb.append(", fileStorageType=").append(fileStorageType);
        sb.append(", filePath=").append(filePath);
        sb.append(", filePathAbs=").append(filePathAbs);
        sb.append(", fileIsdir=").append(fileIsdir);
        sb.append(", fileExt=").append(fileExt);
        sb.append(", fileSize=").append(fileSize);
        sb.append(", fileCategory=").append(fileCategory);
        sb.append(", fileMd5=").append(fileMd5);
        sb.append(", fileSharedType=").append(fileSharedType);
        sb.append(", fileCollected=").append(fileCollected);
        sb.append(", fileCreateTime=").append(fileCreateTime);
        sb.append(", fileUpdateTime=").append(fileUpdateTime);
        sb.append(", delete=").append(delete);
        sb.append(", userId=").append(userId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
