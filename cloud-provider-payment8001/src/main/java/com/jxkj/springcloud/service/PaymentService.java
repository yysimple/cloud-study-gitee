package com.jxkj.springcloud.service;

import com.jxkj.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Param;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
public interface PaymentService {

    /**
     * 新建
     * @param payment
     * @return
     */
    int create(Payment payment);

    /**
     * 通过id查找
     * @param id
     * @return
     */
    Payment getPaymentById(@Param("id") Long id);
}
