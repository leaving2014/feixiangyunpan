package com.fx.pan.factory;

import com.fx.pan.factory.autoconfiguration.FxProperties;
import com.fx.pan.factory.config.TxCosConfig;
import com.fx.pan.factory.constant.StorageTypeEnum;
import com.fx.pan.factory.domain.ThumbImage;
import com.fx.pan.factory.operation.download.Downloader;
import com.fx.pan.factory.operation.download.product.LocalStorageDownloader;
import com.fx.pan.factory.operation.write.Writer;
import com.fx.pan.factory.operation.write.product.LocalStorageWriter;
import com.fx.pan.factory.operation.write.product.TxCosWriter;
import com.fx.pan.factory.preview.Previewer;
import com.fx.pan.factory.preview.product.LocalStoragePreviewer;
import com.fx.pan.factory.upload.Uploader;
import com.fx.pan.factory.upload.product.LocalStorageUploader;

/**
 * @Author leaving
 * @Date 2022/3/4 11:15
 * @Version 1.0
 */


public class FxFactory {
    private ThumbImage thumbImage;

    private String storageType;
    private String localStoragePath;

    private TxCosConfig txCosConfig;

    public FxFactory() {
    }

    public FxFactory(FxProperties fxProperties) {
        this.storageType = fxProperties.getStorageType();
        // this.localStoragePath = fxProperties.getLocalStoragePath();
        this.localStoragePath = fxProperties.getLocalStoragePath();
        this.thumbImage = fxProperties.getThumbImage();
    }

    public Writer getWriter(int storageType) {
        Writer writer = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            writer = new LocalStorageWriter();
        } else if (StorageTypeEnum.COS.getCode() == storageType) {
            writer = new TxCosWriter(txCosConfig);
        }

        return (Writer)writer;
    }

    public Previewer getPreviewer(int storageType) {
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            return new LocalStoragePreviewer(thumbImage);
        }
        // else if (StorageTypeEnum.ALIYUN_OSS.getCode() == storageType) {
        //     return new AliyunOSSPreviewer(aliyunConfig, thumbImage);
        // } else if (StorageTypeEnum.FAST_DFS.getCode() == storageType) {
        //     return fastDFSPreviewer;
        // } else if (StorageTypeEnum.MINIO.getCode() == storageType) {
        //     return new MinioPreviewer(minioConfig, thumbImage);
        // } else if (StorageTypeEnum.QINIUYUN_KODO.getCode() == storageType) {
        //     return new QiniuyunKodoPreviewer(qiniuyunConfig, thumbImage);
        // }
        return null;
    }

    public Uploader getUploader() {
        int type = Integer.parseInt(this.storageType);
        if (StorageTypeEnum.LOCAL.getCode() == type) {
            return new LocalStorageUploader();
        } else {
            return StorageTypeEnum.COS.getCode() == type ? new LocalStorageUploader() : null;
        }
    }

    public Downloader getDownloader(int storageType) {
        Downloader downloader = null;
        if (StorageTypeEnum.LOCAL.getCode() == storageType) {
            return new LocalStorageDownloader();
        }  else if (StorageTypeEnum.COS.getCode() == storageType) {
            return new LocalStorageDownloader();
        }
        return (Downloader)downloader;
    }

    // public Deleter getDeleter(int storageType) {
    //     if (StorageTypeEnum.LOCAL.getCode() == storageType) {
    //         return new LocalStorageDeleter();
    //     }else {
    //         return StorageTypeEnum.COS.getCode() == storageType ? new QiniuyunKodoDeleter(this.qiniuyunConfig) : null;
    //     }
    // }
    //
    // public Reader getReader(int storageType) {
    //     if (StorageTypeEnum.LOCAL.getCode() == storageType) {
    //         return new LocalStorageReader();
    //     } else {
    //         return StorageTypeEnum.COS.getCode() == storageType ? new QiniuyunKodoReader(this.qiniuyunConfig) : null;
    //     }
    // }
    //
    // public Writer getWriter(int storageType) {
    //     if (StorageTypeEnum.LOCAL.getCode() == storageType) {
    //         return new LocalStorageWriter();
    //     } else {
    //         return StorageTypeEnum.COS.getCode() == storageType ? new QiniuyunKodoWriter(this.qiniuyunConfig) : null;
    //     }
    // }
    //
    // public Previewer getPreviewer(int storageType) {
    //     if (StorageTypeEnum.LOCAL.getCode() == storageType) {
    //         return new LocalStoragePreviewer(this.thumbImage);
    //     } else {
    //         return StorageTypeEnum.COS.getCode() == storageType ? new QiniuyunKodoPreviewer(this.qiniuyunConfig, this.thumbImage) : null;
    //     }
    // }
    //
    // public Copier getCopier() {
    //     int type = Integer.parseInt(this.storageType);
    //     if (StorageTypeEnum.LOCAL.getCode() == type) {
    //         return new LocalStorageCopier();
    //     }else {
    //         return StorageTypeEnum.COS.getCode() == type ? new QiniuyunKodoCopier(this.qiniuyunConfig) : null;
    //     }
    // }

}
