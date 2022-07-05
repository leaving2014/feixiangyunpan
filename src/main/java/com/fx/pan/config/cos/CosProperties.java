package com.fx.pan.config.cos;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author leaving
 * @date 2022/5/8 19:25
 * @version 1.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "tencent.cos")
public class CosProperties {

    private  String accessKey;

    private  String secretKey;

    private  String bucketRegion;

    private  String bucketName;

    private  String path;

}
