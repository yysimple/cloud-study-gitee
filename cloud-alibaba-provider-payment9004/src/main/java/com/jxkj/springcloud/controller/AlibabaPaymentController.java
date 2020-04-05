package com.jxkj.springcloud.controller;

import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.entities.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

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

    public static HashMap<Long, Payment> hashMap = new HashMap<>();

    static {
        hashMap.put(1L, new Payment(1L, "dsadsa4d4sa8484dsa6d4"));
        hashMap.put(2L, new Payment(2L, "fdgfdgfdgfdgf54848gfg"));
        hashMap.put(3L, new Payment(3L, "ythh54861fds4g8fg46ew"));
    }

    @GetMapping("/paymentSQL")
    public CommentResult<Payment> paymentSQL(Long id){
        Payment payment = hashMap.get(id);
        CommentResult<Payment> result = new CommentResult(200, "from mysql, serverPort: " + serverPort, payment);
        return result;
    }
}
