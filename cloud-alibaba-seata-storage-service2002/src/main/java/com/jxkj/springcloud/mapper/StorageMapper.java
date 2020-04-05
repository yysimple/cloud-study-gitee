package com.jxkj.springcloud.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Mapper
public interface StorageMapper {

    /**
     * 减库存
     * @param productId
     * @param count
     */
    void decrease(@Param("productId") Long productId, @Param("count") Integer count);
}
