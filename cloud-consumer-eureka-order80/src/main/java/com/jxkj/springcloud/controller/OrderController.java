package com.jxkj.springcloud.controller;

import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.entities.Payment;
import com.jxkj.springcloud.lb.MyLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
@Slf4j
public class OrderController {

    /**
     * 集群服务名
     */
    public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";

    @Autowired
    private MyLoadBalancer myLoadBalancer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @PostMapping("/consumer/payment/create")
    public CommentResult<Payment> create(Payment payment) {
        return restTemplate.postForObject(PAYMENT_URL + "/payment/create", payment, CommentResult.class);
    }

    @GetMapping("/consumer/payment/getPaymentById")
    public CommentResult<Payment> getPaymentById(Long id) {
        return restTemplate.getForObject(PAYMENT_URL + "/payment/getPaymentById?id=" + id, CommentResult.class);
    }

    @GetMapping("/consumer/payment/getPaymentByIdForEntity")
    public CommentResult<Payment> getPaymentByIdForEntity(Long id) {
        ResponseEntity<CommentResult> entity = restTemplate.getForEntity(PAYMENT_URL + "/payment/getPaymentById?id=" + id, CommentResult.class);
        if (entity.getStatusCode().is2xxSuccessful()) {
            log.info("entity: {}", entity);
            return entity.getBody();
        } else {
            return new CommentResult<>(444, "fail ...");
        }
    }

    @GetMapping("/consumer/payment/lb")
    public String getPaymentLB() {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("cloud-payment-service");
        if (serviceInstances == null || serviceInstances.size() <= 0) {
            return null;
        }

        ServiceInstance serviceInstance = myLoadBalancer.instance(serviceInstances);
        URI uri = serviceInstance.getUri();
        return restTemplate.getForObject(uri + "/payment/lb", String.class);
    }

    @GetMapping("/consumer/payment/zipkin")
    public String paymentZipKin(){
        return restTemplate.getForObject("http://localhost:8001" + "/payment/zipkin", String.class);
    }


}
