package com.jxkj.springcloud.controller;

import com.jxkj.springcloud.entities.Account;
import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.service.AccountService;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.units.qual.C;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
public class SeateAccountController {

    @Resource
    private AccountService accountService;

    @PostMapping("/account/decrease")
    public CommentResult decrease(@RequestParam("userId") Long userId,@Param("money") BigDecimal money){
        accountService.decrease(userId, money);
        return new CommentResult(200, "减扣金额成功！");
    }
}
