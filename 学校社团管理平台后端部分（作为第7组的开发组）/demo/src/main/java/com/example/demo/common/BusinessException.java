package com.example.demo.common;

import lombok.Getter;

/**
 * 自定义业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    /**
     * 构造函数
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数（默认错误码）
     * @param message 错误信息
     */
    public BusinessException(String message) {
        this(400, message);
    }
}