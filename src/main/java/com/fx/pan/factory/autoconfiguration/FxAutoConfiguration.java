package com.fx.pan.factory.autoconfiguration;

import com.fx.pan.factory.FxFactory;
import com.fx.pan.factory.FxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author leaving
 * @date 2022/3/10 18:25
 * @version 1.0
 */

@Slf4j
@Configuration
//@ConditionalOnClass(UFOService.class)
@EnableConfigurationProperties({FxProperties.class})
public class FxAutoConfiguration {

    @Resource
    private FxProperties fxProperties;


    @Bean
    public FxFactory FxFactory() {
        FxUtils.LOCAL_STORAGE_PATH = fxProperties.getLocalStoragePath();
        return new FxFactory(fxProperties);
    }


}
