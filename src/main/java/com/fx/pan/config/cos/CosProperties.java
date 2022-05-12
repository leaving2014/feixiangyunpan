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

    // @Value("${tencent.cos.accessKey}")
    private  String accessKey;
    // @Value("${tencent.cos.secretKey}")
    private  String secretKey;
    // @Value("${tencent.cos.bucketRegion}")
    private  String bucketRegion;
    // @Value("${tencent.cos.bucketName}")
    private  String bucketName;
    // @Value("${tencent.cos.path}")
    private  String path;

}
