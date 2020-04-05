package com.jxkj.springcloud.service;

import com.jxkj.springcloud.entities.CommentResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
public interface AccountService {

    /**
     * 减账户的钱
     * @param userId
     * @param money
     * @return
     */
    void decrease(Long userId, BigDecimal money);
}
