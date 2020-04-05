package com.jxkj.springcloud.service;

import com.jxkj.springcloud.entities.CommentResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@FeignClient(value = "cloud-seata-storage-service")
public interface StorageService {

    /**
     * 减库存
     * @param productId
     * @param count
     * @return
     */
    @PostMapping("/storage/decrease")
    CommentResult decrease(@RequestParam("productId") Long productId, @RequestParam("count") Integer count);

}
