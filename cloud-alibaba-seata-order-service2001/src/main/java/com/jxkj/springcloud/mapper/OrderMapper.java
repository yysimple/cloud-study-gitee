package com.jxkj.springcloud.mapper;

import com.jxkj.springcloud.entities.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Mapper
public interface OrderMapper {

    /**
     * 创建订单
     * @param order
     */
    void create(Order order);

    /**
     * 修改订单状态
     * @param userId
     * @param status
     */
    void update(@Param("userId") Long userId, @Param("status") Integer status);


}
