package com.example.demo.advice;

import com.example.demo.util.Response;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应拦截器，自动包装响应结果
 */
@RestControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否需要对响应进行包装
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 只对非Response类型的返回值进行包装
        return !returnType.getParameterType().isAssignableFrom(Response.class);
    }

    /**
     * 包装响应结果
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 特殊处理String类型，避免类型转换异常
        if (body instanceof String) {
            // 返回JSON字符串形式的Response
            return "{\"code\":200,\"message\":\"success\",\"data\":\"" + body + "\"}";
        }
        return Response.success(body);
    }
}