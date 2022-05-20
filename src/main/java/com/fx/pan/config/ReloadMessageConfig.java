package com.fx.pan.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

/**
 * @author leaving
 * @version 1.0
 * @date 2022/5/14 21:42
 */

@Configuration
public class ReloadMessageConfig {
    /**
     * 自定义错误信息
     * @return
     */
    @Bean
    public MessageSource messageSource() {
        Locale.setDefault(Locale.CHINA);
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        //中文提示信息配置文件
        messageSource.addBasenames("classpath:messages_zh_CN");
        return messageSource;
    }
}
