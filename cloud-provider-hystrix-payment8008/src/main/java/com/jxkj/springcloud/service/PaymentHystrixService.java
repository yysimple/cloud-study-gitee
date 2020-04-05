package com.jxkj.springcloud.service;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.TimeUnit;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Service
public class PaymentHystrixService {

    /**
     * 正常访问的方法
     * @param id
     * @return
     */
    public String paymentInfoOk(Integer id){
        return "线程池==：" + Thread.currentThread().getName() + "paymentInfoOk_id==: " + id + "\t" + "O(∩_∩)O哈哈~";
    }

    /**
     * 模拟延时
     * @HystrixCommand: 用来做服务请求超时的降级，出现问题通过fallbackMethod 来指定后续处理的方法
     * commandProperties： 用来限制请求时间，超过就降级
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    })
    public String paymentTimeout(Integer id){
        // 程序暂停3s, 模拟大的业务，超过3s以上的业务
        int timeout = 3;
        try {
            TimeUnit.SECONDS.sleep(timeout);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return "线程池==：" + Thread.currentThread().getName() + "paymentTimeout_id==: " + id + "\t" + "O(∩_∩)O哈哈~ == 耗时" + timeout +"秒";
    }

    public String paymentTimeoutHandler(Integer id){
        return "线程池==：" + Thread.currentThread().getName() + "paymentTimeoutHandler==: " + id + "┭┮﹏┭┮ == 耗时";
    }

    /**
     * 服务熔断：====
     */
    @HystrixCommand(fallbackMethod = "paymentCircuitBreakerFallBack", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"), // 是否开启短路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"), // 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"), // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60") // 失败率达到什么概率跳闸
    })
    public String paymentCircuitBreaker(@RequestParam("id") Integer id){
        if (id < 0) {
            throw new RuntimeException("******* id不能为负数 ******");
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName() + "\t" + "调用成功，流水号： " + serialNumber;
    }

    public String paymentCircuitBreakerFallBack(@RequestParam("id") Integer id){
        return "id 不能为负数，请稍后再试， o(╥﹏╥)o ~~ id: " + id;
    }
}
