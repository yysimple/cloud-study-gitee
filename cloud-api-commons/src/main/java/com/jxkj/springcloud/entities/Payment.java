package com.jxkj.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 流水号
     */
    private String serial;
}
