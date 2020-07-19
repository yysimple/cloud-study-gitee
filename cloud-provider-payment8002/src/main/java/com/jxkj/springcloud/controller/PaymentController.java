package com.jxkj.springcloud.controller;

import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.entities.Payment;
import com.jxkj.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
@Slf4j
public class PaymentController {

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @PostMapping("/payment/create")
    public CommentResult create(@RequestBody Payment payment) {
        int result = paymentService.create(payment);
        log.info("****RESULT****: {}", result);
        if (result > 0) {
            return new CommentResult(200, "success port: " + serverPort, result);
        } else {
            return new CommentResult(444, "fail", null);
        }
    }

    @GetMapping("/payment/getPaymentById")
    public CommentResult getPaymentById(@RequestParam("id") Long id) {
        log.info("id: {}", id);
        Payment payment = paymentService.getPaymentById(id);
        log.info("****RESULT****: {}", payment);
        if (payment != null) {
            return new CommentResult(200, "success port: " + serverPort, payment);
        } else {
            return new CommentResult(444, "fail, search id: " + id, null);
        }
    }

    @GetMapping("/payment/discovery")
    public Object getDiscovery() {
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            log.info("**** service: " + service);
        }

        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance serviceInstance : serviceInstances) {
            log.info(serviceInstance.getInstanceId() + "\t" + serviceInstance.getHost() + "\t" + serviceInstance.getUri());
        }

        return this.discoveryClient;
    }

    @GetMapping("/payment/lb")
    public String getPaymentLB() {
        return serverPort;
    }

    @GetMapping("/payment/zipkin")
    public String paymentZipKin() {
        return "wo shi ZipKin, welcome to you!!! O(∩_∩)O哈哈~";
    }

    @GetMapping("/lb")
    public String lb() {
        return "测试StripPrefix=1";
    }
}
