package com.jxkj.springcloud.service.impl;

import com.jxkj.springcloud.entities.Order;
import com.jxkj.springcloud.mapper.OrderMapper;
import com.jxkj.springcloud.service.AccountService;
import com.jxkj.springcloud.service.OrderService;
import com.jxkj.springcloud.service.StorageService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private StorageService storageService;

    @Resource
    private AccountService accountService;

    @Override
    @GlobalTransactional(name = "cloud_seata-create", rollbackFor = Exception.class)
    public void create(Order order) {
        log.info("---------> 开始创建订单");
        orderMapper.create(order);

        log.info("---------> 订单微服务开始调用库存，做数量减");
        storageService.decrease(order.getProductId(), order.getCount());
        log.info("---------> 订单微服务开始调用库存，做数量减扣 end....");

        log.info("---------> 订单微服务开始调用账户，做money减");
        accountService.decrease(order.getUserId(), order.getMoney());
        log.info("---------> 订单微服务开始调用账户，做money减 end....");

        log.info("---------> 修改订单状态开始");
        orderMapper.update(order.getUserId(), 0);
        log.info("---------> 修改订单状态结束");

        log.info("---------> 完成订单");
    }
}
