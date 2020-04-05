package com.jxkj.springcloud.service;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
public interface StorageService {

    /**
     * 减库存
     * @param productId
     * @param count
     */
    void decrease(Long productId, Integer count);
}
