package com.jxkj.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Configuration
public class ApplicationContextConfig {

    /**
     * 将 RestTemplate 注入到Spring的容器中
     * @LoadBalanced: 开启负载均衡,使用的是Ribbon自带的
     * @return
     */
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
