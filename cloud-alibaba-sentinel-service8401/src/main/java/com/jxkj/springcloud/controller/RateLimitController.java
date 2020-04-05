package com.jxkj.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.entities.Payment;
import com.jxkj.springcloud.myhandler.CustomerBlockHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
public class RateLimitController {

    @GetMapping("/byResource")
    @SentinelResource(value = "byResource", blockHandler = "handleException")
    public CommentResult byResource(){
        return new CommentResult(200, "按资源名称限流ok", new Payment(2020L, "serial002"));
    }

    public CommentResult handleException(BlockException b){
        return new CommentResult(444, b.getClass().getCanonicalName() + "\t 服务不可用");
    }

    @GetMapping("/rateLimit/byUrl")
    @SentinelResource(value = "byUrl")
    public CommentResult byUrl(){
        return new CommentResult(200, "按url资源进行限流测试ok", new Payment(2020L, "serial002"));
    }

    @GetMapping("/rateLimit/customBlockHandler")
    @SentinelResource(value = "customBlockHandler",
                    blockHandlerClass = CustomerBlockHandler.class,
                    blockHandler = "handlerException1")
    public CommentResult customBlockHandler(){
        return new CommentResult(200, "按用户自定义", new Payment(2020L, "serial003"));
    }
}
