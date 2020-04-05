package com.jxkj.springcloud.service;

import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Component
public class PaymentHystrixServiceImpl implements PaymentHystrixService {

    @Override
    public String paymentInfoOk(Integer id) {
        return "PaymentHystrixServiceImpl -- fallback -- paymentInfoOk";
    }

    @Override
    public String paymentTimeout(Integer id) {
        return "PaymentHystrixServiceImpl -- fallback -- paymentTimeout";
    }
}
