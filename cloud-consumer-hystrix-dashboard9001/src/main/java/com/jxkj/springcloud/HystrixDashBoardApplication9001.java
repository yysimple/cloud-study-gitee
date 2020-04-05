package com.jxkj.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashBoardApplication9001 {
    public static void main(String[] args) {
        SpringApplication.run(HystrixDashBoardApplication9001.class, args);
    }
}
