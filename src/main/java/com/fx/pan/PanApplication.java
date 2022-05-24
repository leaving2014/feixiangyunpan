package com.fx.pan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

/**
 * @author leaving
 * @version 1.0
 * @date 2021/11/22 14:25
 */

@SpringBootApplication(
        // exclude = {SecurityAutoConfiguration.class}
        // exclude = { DataSourceAutoConfiguration.class }
)
@MapperScan("com.fx.pan.mapper")
@EnableAsync
@EnableTransactionManagement
public class PanApplication {
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setIgnoreUnresolvablePlaceholders(true);
        return c;
    }

    public static void main(String[] args) {
        SpringApplication.run(PanApplication.class, args);
    }

}
