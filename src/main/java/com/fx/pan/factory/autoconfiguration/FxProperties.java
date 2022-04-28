package com.fx.pan.factory.autoconfiguration;

import com.fx.pan.factory.domain.ThumbImage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * @Author leaving
 * @Date 2022/3/4 11:59
 * @Version 1.0
 */

@ConfigurationProperties(
        prefix = "fx"
)
@PropertySource("classpath:application.properties")
public class FxProperties {

    private String storageType;
    private String localStoragePath;
    private ThumbImage thumbImage = new ThumbImage();

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getLocalStoragePath() {
        return localStoragePath;
    }

    public void setLocalStoragePath(String localStoragePath) {
        this.localStoragePath = localStoragePath;
    }

    public ThumbImage getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(ThumbImage thumbImage) {
        this.thumbImage = thumbImage;
    }

    @Override
    public String toString() {
        return "FxProperties{" +
                "storageType='" + storageType + '\'' +
                ", localStoragePath='" + localStoragePath + '\'' +
                ", thumbImage=" + thumbImage +
                '}';
    }
}
