package com.example.demo.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 全局统一响应格式
 */
@Data
@AllArgsConstructor
public class Response<T> {
    /**
     * 状态码
     */
    private int code;
    /**
     * 消息
     */
    private String message;
    /**
     * 数据
     */
    private T data;
    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 构造函数，自动设置当前时间戳
     */
    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 成功响应
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Response<T> success() {
        return new Response<>(200, "success", null);
    }

    /**
     * 失败响应
     */
    public static <T> Response<T> fail(int code, String message) {
        return new Response<>(code, message, null);
    }

    /**
     * 失败响应（默认状态码）
     */
    public static <T> Response<T> fail(String message) {
        return new Response<>(500, message, null);
    }
}