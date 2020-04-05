package com.jxkj.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.entities.Payment;
import com.jxkj.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
@Slf4j
public class AlibabaOrderController {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private PaymentService paymentService;

    public static final String SERVICE_URL = "http://nacos-cloud-payment-provider";

    @Value("${service-url.nacos-user-service}")
    private String serverUrl;

    @GetMapping("/consumer/payment/nacos")
    public String getPaymentInfo(@RequestParam("id") Integer id){
        return restTemplate.getForObject(SERVICE_URL + "/paymentSQL?id=" + id, String.class);
    }

    @GetMapping("/consumer/fallback")
    /**
     * fallback: 负责的是java的业务异常
     * blockHandler: 这是sentinel控制台的限流方式，在1s一次的时候还是会报对应的异常，但是超过阈值 则会报 sentinel的异常
     * 当两个都加上的时候，两种处理方式会同时进行
     */

    // @SentinelResource(value = "fallback", fallback = "handlerFallback")
    @SentinelResource(value = "fallback", blockHandler = "blockHandler", fallback = "handlerFallback")
    public CommentResult<Payment> fallback(@RequestParam("id") Long id){
        CommentResult<Payment> result = restTemplate.getForObject(SERVICE_URL + "/paymentSQL?id=" + id, CommentResult.class, id);
        if (id == 4) {
            throw new IllegalArgumentException("非法参数异常...");
        }else if (result.getData() == null) {
            throw new NullPointerException("没有对应的id，空指针异常...");
        }
        return result;
    }

    public CommentResult handlerFallback(Long id, Throwable e){
        Payment payment = new Payment(id, null);
        return new CommentResult(444, "兜底异常处理" + e.getMessage(), payment);
    }

    public CommentResult blockHandler(Long id, BlockException b){
        Payment payment = new Payment(id, null);
        return new CommentResult(445, "无此流水，这是sentinel控制台限流" + b.getMessage(), payment);
    }

    @GetMapping("/consumer/paymentSQL")
    CommentResult<Payment> paymentSQL(@RequestParam("id") Long id){
        return paymentService.paymentSQL(id);
    }
}
