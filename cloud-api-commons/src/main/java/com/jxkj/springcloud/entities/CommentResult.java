package com.jxkj.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResult<T> {

    private Integer code;

    private String message;

    private T data;

    public CommentResult(Integer code, String message){
        this(code, message, null);
    }

}
