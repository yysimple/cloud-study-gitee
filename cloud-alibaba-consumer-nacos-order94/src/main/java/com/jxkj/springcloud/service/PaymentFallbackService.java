package com.jxkj.springcloud.service;

import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.entities.Payment;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Component
public class PaymentFallbackService implements PaymentService{

    @Override
    public CommentResult<Payment> paymentSQL(Long id) {
        return new CommentResult(4414, "服务降级返回，---paymentFallbackService", new Payment(id, "error"));
    }
}
