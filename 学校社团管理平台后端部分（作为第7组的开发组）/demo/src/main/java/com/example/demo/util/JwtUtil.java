package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;
    
    // 延迟初始化SecretKey
    private SecretKey secretKey;
    
    private SecretKey getSecretKey() {
        if (secretKey == null) {
            // 如果提供的密钥长度不足，Keys.secretKeyFor会自动生成足够长度的密钥
            secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        }
        return secretKey;
    }

    @Value("${jwt.expiration}")
    private Long jwtExpirationInMs;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpirationInMs;

    // 从JWT令牌中获取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从JWT令牌中获取用户ID
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> Long.parseLong(claims.get("id").toString()));
    }

    // 从JWT令牌中获取用户角色
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // 从JWT令牌中提取特定的声明
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 从JWT令牌中提取所有声明
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 生成JWT令牌
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("role", role);
        return createToken(claims, username);
    }

    // 创建JWT令牌
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSecretKey())
                .compact();
    }

    // 生成刷新令牌
    public String generateRefreshToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("role", role);
        return createRefreshToken(claims, username);
    }

    // 创建刷新令牌
    private String createRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationInMs))
                .signWith(getSecretKey())
                .compact();
    }

    // 验证JWT令牌是否有效
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // 检查JWT令牌是否已过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 从JWT令牌中获取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}