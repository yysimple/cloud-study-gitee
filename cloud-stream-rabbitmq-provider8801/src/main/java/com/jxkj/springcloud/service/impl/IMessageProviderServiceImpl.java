package com.jxkj.springcloud.service.impl;

import com.jxkj.springcloud.service.IMessageProviderService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.util.UUID;


/**
 * 功能描述： 定义消息的推送管道
 *
 * @author wcx
 * @version 1.0
 */
@EnableBinding(Source.class)
public class IMessageProviderServiceImpl implements IMessageProviderService {

    /**
     * 消息发送管道 ------zhuzhzuzhuzhzuz:  这里只能写 output 要不然会报错
     */
    @Resource
    private MessageChannel output;

    @Override
    public String send() {
        String serial = UUID.randomUUID().toString();
        output.send(MessageBuilder.withPayload(serial).build());
        System.out.println("**** serial ****: " + serial);
        return serial;
    }
}
