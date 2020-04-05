package com.jxkj.springcloud.myhandler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.jxkj.springcloud.entities.CommentResult;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
public class CustomerBlockHandler {

    public static CommentResult handlerException1(BlockException b){
        return new CommentResult(4444, "按用户自定义，global handler exception ---- 001");
    }
    public static CommentResult handlerException2(BlockException b){
        return new CommentResult(4444, "按用户自定义，global handler exception ---- 002");
    }
}
