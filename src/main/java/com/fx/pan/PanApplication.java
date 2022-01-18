package com.fx.pan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author leaving
 */
@SpringBootApplication(
        // exclude = {SecurityAutoConfiguration.class}
        // exclude = { DataSourceAutoConfiguration.class }
)
@MapperScan("com.fx.pan.mapper")
public class PanApplication {

    public static void main(String[] args) {
        SpringApplication.run(PanApplication.class, args);
    }

}
