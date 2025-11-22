package com.example.demo.advice;

import com.example.demo.util.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response<?> handleAuthenticationException(AuthenticationException e) {
        if (e instanceof BadCredentialsException) {
            return Response.fail(401, "用户名或密码错误");
        }
        return Response.fail(401, "认证失败");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Response<?> handleRuntimeException(RuntimeException e) {
        return Response.fail(e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Response.fail(400, message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Response<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Response.fail(400, message);
    }

    /**
     * 处理数据库约束异常
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Response<?> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        String message = "数据操作失败：" + e.getMessage();
        return Response.fail(400, message);
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public Response<?> handleException(Exception e) {
        e.printStackTrace();
        return Response.fail(500, "服务器内部错误");
    }
}