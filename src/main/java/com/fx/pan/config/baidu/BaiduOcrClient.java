package com.fx.pan.config.baidu;

import com.baidu.aip.ocr.AipOcr;
import com.fx.pan.config.cos.CosProperties;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author leaving
 * @date 2022/5/8 19:51
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(BaiduOcrProperties.class)
public class BaiduOcrClient {
    @Autowired
    private BaiduOcrProperties baiduOcrProperties;

    @Bean
    public AipOcr aipOcr() {
        AipOcr aipOcr = new AipOcr(baiduOcrProperties.getApp_id(), baiduOcrProperties.getApp_id(),
                baiduOcrProperties.getSecret_key());
        return aipOcr;
    }
}
