package com.jxkj.springcloud.service.impl;

import com.jxkj.springcloud.entities.Storage;
import com.jxkj.springcloud.mapper.StorageMapper;
import com.jxkj.springcloud.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Resource
    private StorageMapper storageMapper;

    @Override
    public void decrease(Long productId, Integer count) {
        log.info("---------> storage-service 减库存开始");
        storageMapper.decrease(productId, count);
        log.info("---------> storage-service 减库存结束");
    }
}
