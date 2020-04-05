package com.jxkj.springcloud.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Configuration
@MapperScan(basePackages = "com.jxkj.springcloud.mapper")
public class MyBatisConfig {
}
