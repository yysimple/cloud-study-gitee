package com.jxkj.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AlibabaProviderApplication9001 {
    public static void main(String[] args) {
        SpringApplication.run(AlibabaProviderApplication9001.class, args);
    }
}
