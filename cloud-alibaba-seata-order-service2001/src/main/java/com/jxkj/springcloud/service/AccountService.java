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
@FeignClient(value = "cloud-seata-account-service")
public interface AccountService {

    /**
     * 减账户的钱
     * @param userId
     * @param money
     * @return
     */
    @PostMapping("/account/decrease")
    CommentResult decrease(@RequestParam("userId") Long userId, @RequestParam("money") BigDecimal money);
}
