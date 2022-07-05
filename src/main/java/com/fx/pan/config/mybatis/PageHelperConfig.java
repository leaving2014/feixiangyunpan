package com.fx.pan.config.mybatis;

import com.github.pagehelper.PageHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author leaving
 * @date 2022/5/2 14:00
 * @version 1.0
 */

@Configuration
@ConditionalOnProperty(name = "pagehelper.supportMethodsArguments")
public class PageHelperConfig {

    /**
     * 注入pageHelper配置
     */
    @Bean
    public PageHelper getPageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        // TODO 根据自己使用的数据库配置
        properties.setProperty("helperDialect", "mariadb");
        // properties.setProperty("helperDialect", "mysql");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}
