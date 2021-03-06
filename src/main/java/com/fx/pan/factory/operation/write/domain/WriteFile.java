package com.fx.pan.factory.operation.write.domain;

/**
 * @author leaving
 * @date 2022/3/19 16:29
 * @version 1.0
 */

public class WriteFile {
    private String fileUrl;
    private long fileSize;

    public WriteFile() {
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileUrl(final String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setFileSize(final long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof WriteFile)) {
            return false;
        } else {
            WriteFile other = (WriteFile)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getFileSize() != other.getFileSize()) {
                return false;
            } else {
                Object this$fileUrl = this.getFileUrl();
                Object other$fileUrl = other.getFileUrl();
                if (this$fileUrl == null) {
                    if (other$fileUrl != null) {
                        return false;
                    }
                } else if (!this$fileUrl.equals(other$fileUrl)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof WriteFile;
    }

    @Override
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        long $fileSize = this.getFileSize();
         result = result * 59 + (int)($fileSize >>> 32 ^ $fileSize);
        Object $fileUrl = this.getFileUrl();
        result = result * 59 + ($fileUrl == null ? 43 : $fileUrl.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "WriteFile(fileUrl=" + this.getFileUrl() + ", fileSize=" + this.getFileSize() + ")";
    }
}
