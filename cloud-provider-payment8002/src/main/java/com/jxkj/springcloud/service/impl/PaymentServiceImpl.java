package com.jxkj.springcloud.service.impl;

import com.jxkj.springcloud.mapper.PaymentDao8002;
import com.jxkj.springcloud.entities.Payment;
import com.jxkj.springcloud.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentDao8002 paymentDao8002;

    @Override
    public int create(Payment payment) {
        return paymentDao8002.create(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentDao8002.getPaymentById(id);
    }
}
