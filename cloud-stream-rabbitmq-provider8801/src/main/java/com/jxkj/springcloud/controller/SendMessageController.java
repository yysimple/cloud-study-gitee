package com.jxkj.springcloud.controller;

import com.jxkj.springcloud.service.IMessageProviderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
public class SendMessageController {

    @Resource
    private IMessageProviderService iMessageProviderService;

    @GetMapping("/sendMessage")
    public String sendMessage(){
        return iMessageProviderService.send();
    }
}
