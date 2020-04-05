package com.jxkj.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
public class AlibabaPaymentController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/payment/nacos")
    public String getPaymentById(@RequestParam("id") Integer id){
        return "nacos registry center, serverPort: " + serverPort + "\t  id: " + id;
    }
}
