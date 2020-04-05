package com.jxkj.springcloud.lb;
import org.springframework.cloud.client.ServiceInstance;
import java.util.List;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
public interface MyLoadBalancer {
    /**
     * 获取所有实例
     * @param serviceInstances
     * @return
     */
    ServiceInstance instance(List<ServiceInstance> serviceInstances);
}
