package com.jxkj.springcloud.service;

import com.jxkj.springcloud.entities.Order;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
public interface OrderService {

    /**
     * 创建订单
     * @param order
     */
    void create(Order order);
}
