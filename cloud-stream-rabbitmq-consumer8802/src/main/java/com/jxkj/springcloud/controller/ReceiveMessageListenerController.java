package com.jxkj.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Component
@EnableBinding(Sink.class)
public class ReceiveMessageListenerController {

    @Value("${server.port}")
    private String serverPort;

    /**
     * 前面提供的是String类型的数据所以 <String>
     * @param message
     */
    @StreamListener(Sink.INPUT)
    public void input(Message<String> message){
        // 消息提供方使用的是withPayload，所以这里使用getPayload
        System.out.println("消费者1号，------>接收到消息：" + message.getPayload() + "\t port:" + serverPort);
    }

}
