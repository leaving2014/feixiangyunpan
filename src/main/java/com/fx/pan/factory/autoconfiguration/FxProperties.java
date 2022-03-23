package com.fx.pan.factory.autoconfiguration;

import com.fx.pan.factory.domain.ThumbImage;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author leaving
 * @Date 2022/3/4 11:59
 * @Version 1.0
 */

@Data
@ConfigurationProperties(
        prefix = "fx"
)
public class FxProperties {

    private String storageType;
    private String localStoragePath;
    private ThumbImage thumbImage = new ThumbImage();

}
