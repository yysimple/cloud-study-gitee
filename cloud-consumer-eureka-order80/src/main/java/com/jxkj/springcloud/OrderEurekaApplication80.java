package com.jxkj.springcloud;

import com.jxkj.myRule.MyRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "cloud-payment-service", configuration = MyRule.class)
public class OrderEurekaApplication80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderEurekaApplication80.class, args);
    }
}
