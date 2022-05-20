package com.fx.pan.config.baidu;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author leaving
 * @date 2022/5/8 19:35
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "baidu.ocr")
public class BaiduOcrProperties {
    private String APP_ID;
    private String API_KEY;
    private String SECRET_KEY;
}
