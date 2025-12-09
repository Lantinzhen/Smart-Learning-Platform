package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private Integer code;    // 状态码
    private String message;  // 响应消息
    private T data;          // 响应数据

    /**
     * 成功响应
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    /**
     * 成功响应（带自定义消息）
     */
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Response<T> fail(Integer code, String message) {
        return new Response<>(code, message, null);
    }

    /**
     * 失败响应（默认错误码）
     */
    public static <T> Response<T> fail(String message) {
        return new Response<>(400, message, null);
    }


}
