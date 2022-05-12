package com.fx.pan.factory.domain;

/**
 * @author leaving
 * @date 2022/3/19 16:38
 * @version 1.0
 */

public class TxCos {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String objectName;

    public TxCos() {
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getAccessKeyId() {
        return this.accessKeyId;
    }

    public String getAccessKeySecret() {
        return this.accessKeySecret;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    public void setAccessKeyId(final String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setAccessKeySecret(final String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void setBucketName(final String bucketName) {
        this.bucketName = bucketName;
    }

    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TxCos)) {
            return false;
        } else {
            TxCos other = (TxCos)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$endpoint = this.getEndpoint();
                    Object other$endpoint = other.getEndpoint();
                    if (this$endpoint == null) {
                        if (other$endpoint == null) {
                            break label71;
                        }
                    } else if (this$endpoint.equals(other$endpoint)) {
                        break label71;
                    }

                    return false;
                }

                Object this$accessKeyId = this.getAccessKeyId();
                Object other$accessKeyId = other.getAccessKeyId();
                if (this$accessKeyId == null) {
                    if (other$accessKeyId != null) {
                        return false;
                    }
                } else if (!this$accessKeyId.equals(other$accessKeyId)) {
                    return false;
                }

                label57: {
                    Object this$accessKeySecret = this.getAccessKeySecret();
                    Object other$accessKeySecret = other.getAccessKeySecret();
                    if (this$accessKeySecret == null) {
                        if (other$accessKeySecret == null) {
                            break label57;
                        }
                    } else if (this$accessKeySecret.equals(other$accessKeySecret)) {
                        break label57;
                    }

                    return false;
                }

                Object this$bucketName = this.getBucketName();
                Object other$bucketName = other.getBucketName();
                if (this$bucketName == null) {
                    if (other$bucketName != null) {
                        return false;
                    }
                } else if (!this$bucketName.equals(other$bucketName)) {
                    return false;
                }

                Object this$objectName = this.getObjectName();
                Object other$objectName = other.getObjectName();
                if (this$objectName == null) {
                    if (other$objectName == null) {
                        return true;
                    }
                } else if (this$objectName.equals(other$objectName)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TxCos;
    }

    @Override
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $endpoint = this.getEndpoint();
         result = result * 59 + ($endpoint == null ? 43 : $endpoint.hashCode());
        Object $accessKeyId = this.getAccessKeyId();
        result = result * 59 + ($accessKeyId == null ? 43 : $accessKeyId.hashCode());
        Object $accessKeySecret = this.getAccessKeySecret();
        result = result * 59 + ($accessKeySecret == null ? 43 : $accessKeySecret.hashCode());
        Object $bucketName = this.getBucketName();
        result = result * 59 + ($bucketName == null ? 43 : $bucketName.hashCode());
        Object $objectName = this.getObjectName();
        result = result * 59 + ($objectName == null ? 43 : $objectName.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AliyunOSS(endpoint=" + this.getEndpoint() + ", accessKeyId=" + this.getAccessKeyId() + ", accessKeySecret=" + this.getAccessKeySecret() + ", bucketName=" + this.getBucketName() + ", objectName=" + this.getObjectName() + ")";
    }
}
