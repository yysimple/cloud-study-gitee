package com.jxkj.springcloud.service.impl;

import com.jxkj.springcloud.mapper.AccountMapper;
import com.jxkj.springcloud.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public void decrease(Long userId, BigDecimal money) {
        log.info("---------> account-service 开始减金额");
        try{
            TimeUnit.SECONDS.sleep(20);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        accountMapper.decrease(userId, money);
        log.info("---------> account-service 减金额结束");
    }
}
