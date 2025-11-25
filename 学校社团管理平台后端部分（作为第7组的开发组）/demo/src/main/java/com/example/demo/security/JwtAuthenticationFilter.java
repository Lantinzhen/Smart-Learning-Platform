package com.example.demo.security;

import com.example.demo.common.BusinessException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 获取Authorization头
            String authorizationHeader = request.getHeader("Authorization");

            // 跳过登录和注册接口的认证
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/api/v1/auth")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 检查token格式
            if (authorizationHeader == null) {
                // 提供更明确的错误信息，告诉用户缺少Authorization头
                throw new BusinessException(401, "缺少认证信息，请在请求头中添加 Authorization: Bearer {token}");
            }

            // 提取token（支持带Bearer前缀和不带前缀的情况）
            String token;
            if (authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
            } else {
                // 直接使用整个字符串作为token
                token = authorizationHeader;
            }
            
            // 检查token是否在黑名单中
            if (tokenBlacklist.isInBlacklist(token)) {
                throw new BusinessException(401, "token已被注销，请重新登录");
            }

            // 验证token
            if (!jwtUtil.validateToken(token)) {
                throw new BusinessException(401, "token已过期或无效");
            }

            // 解析token获取用户信息
            Claims claims = jwtUtil.parseToken(token);
            String userId = claims.get("userId", String.class);
            String role = claims.get("role", String.class);

            // 创建UserDetails
            UserDetails userDetails = new User(userId, "", new ArrayList<>());

            // 设置认证信息到SecurityContext
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 设置用户信息到请求属性中
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            if (e instanceof BusinessException) {
                // 对于业务异常，我们将在全局异常处理器中处理
                throw e;
            }
            throw new BusinessException(401, "认证失败");
        }

        filterChain.doFilter(request, response);
    }
}