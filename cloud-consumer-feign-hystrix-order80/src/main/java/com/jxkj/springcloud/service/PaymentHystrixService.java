package com.jxkj.springcloud.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Service
@FeignClient(value = "hystrix-cloud-payment-service", fallback = PaymentHystrixServiceImpl.class)
public interface PaymentHystrixService {

    @GetMapping("/payment/hystrix/paymentInfoOk")
    String paymentInfoOk(@RequestParam("id") Integer id);

    @GetMapping("/payment/hystrix/paymentTimeout")
    String paymentTimeout(@RequestParam("id") Integer id);
}
