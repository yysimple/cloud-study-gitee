package com.jxkj.springcloud.controller;

import com.jxkj.springcloud.service.PaymentHystrixService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class PaymentHystrixController {

    @Autowired
    private PaymentHystrixService paymentHystrixService;

    @Value("${server.port}")
    private String serverPort;


    @GetMapping("/payment/hystrix/paymentInfoOk")
    public String paymentInfoOk(@RequestParam("id") Integer id){
        return paymentHystrixService.paymentInfoOk(id);
    }

    @GetMapping("/payment/hystrix/paymentTimeout")
    public String paymentTimeout(@RequestParam("id") Integer id){
        return paymentHystrixService.paymentTimeout(id);
    }

    @GetMapping("/payment/circuit")
    public String paymentCircuitBreaker(@RequestParam("id") Integer id){
        return paymentHystrixService.paymentCircuitBreaker(id);
    }
}
