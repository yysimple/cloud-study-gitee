package com.jxkj.springcloud.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Configuration
public class GatewayRouterConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        RouteLocatorBuilder.Builder routes = builder.routes();
        // 进行路由转发
        routes.route("path-baidu-guonei", r -> r.path("/guonei").uri("http://news.baidu.com/guonei")).build();
        //routes.route("path-baidu-guoji", r -> r.path("/guoji").uri("http://news.baidu.com/guoji")).build();
        return routes.build();
    }
}
