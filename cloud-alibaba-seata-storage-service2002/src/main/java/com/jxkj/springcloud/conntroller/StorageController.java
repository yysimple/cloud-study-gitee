package com.jxkj.springcloud.conntroller;

import com.jxkj.springcloud.entities.CommentResult;
import com.jxkj.springcloud.service.StorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@RestController
public class StorageController {

    @Resource
    private StorageService storageService;

    @PostMapping("/storage/decrease")
    public CommentResult decrease(Long productId, Integer count){
        storageService.decrease(productId, count);
        return new CommentResult(200, "扣除库存成功!");
    }
}
