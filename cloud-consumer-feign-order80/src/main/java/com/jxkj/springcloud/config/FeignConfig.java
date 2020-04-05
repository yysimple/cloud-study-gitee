package com.jxkj.springcloud.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Configuration
public class FeignConfig {

    /**
     * 开启feign的详细日志
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
