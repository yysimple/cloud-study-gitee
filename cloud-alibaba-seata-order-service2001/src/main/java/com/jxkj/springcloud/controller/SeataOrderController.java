package com.jxkj.springcloud.controller;

import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.entities.Order;
import com.jxkj.springcloud.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
public class SeataOrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/order/create")
    public CommentResult create(Order order){
        orderService.create(order);
        return new CommentResult(200, "订单创建成功");
    }
}
