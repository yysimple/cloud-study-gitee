package com.jxkj.springcloud.service;

import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Component
@FeignClient(value = "cloud-payment-service")
public interface PaymentFeignService {

    /**
     * 将url注册到feign中， 也就是封装restTemplate
     * @param id
     * @return
     */
    @GetMapping("/payment/getPaymentById")
    CommentResult<Payment> getPaymentById(@RequestParam("id") Long id);

    /**
     * 模拟延时操作
     * @return
     */
    @GetMapping("/payment/feign/timeout")
    String paymentFeignTimeout();
}
