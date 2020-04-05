package com.jxkj.springcloud.service;

import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@FeignClient(value = "nacos-cloud-payment-provider", fallback = PaymentFallbackService.class)
public interface PaymentService {

    @GetMapping("/paymentSQL")
    CommentResult<Payment> paymentSQL(@RequestParam("id") Long id);
}
