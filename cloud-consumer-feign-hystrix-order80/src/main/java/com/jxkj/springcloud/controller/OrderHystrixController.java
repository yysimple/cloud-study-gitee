package com.jxkj.springcloud.controller;

import com.jxkj.springcloud.service.PaymentHystrixService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "paymentGlobalTimeoutHandler")
public class OrderHystrixController {

    @Autowired
    private PaymentHystrixService paymentHystrixService;

    @GetMapping("/consumer/payment/hystrix/paymentInfoOk")
    public String paymentInfoOk(@RequestParam("id") Integer id){
        return paymentHystrixService.paymentInfoOk(id);
    }

    @GetMapping("/consumer/payment/hystrix/paymentTimeout")
    /*@HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })*/
    @HystrixCommand
    public String paymentTimeout(@RequestParam("id") Integer id){
        return paymentHystrixService.paymentTimeout(id);
    }

    public String paymentTimeoutHandler(@RequestParam("id") Integer id){
        return "我是消费端的80接口，对方的支付系统繁忙，请稍后再试...┭┮﹏┭┮...";
    }

    /**
     * 全局降级处理
     * @return
     */
    public String paymentGlobalTimeoutHandler(){
        return "Global全局降级处理，请稍后再试.../(⊙︿⊙)/";
    }
}
