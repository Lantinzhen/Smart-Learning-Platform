package com.example.demo.util;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=your-256-bit-secret-key-change-this-in-production",
        "jwt.expiration=3600000",
        "jwt.refresh.expiration=86400000"
})
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testJwtUtil() {
        // 测试生成和验证令牌
        Long userId = 1L;
        String username = "testuser";
        String role = "STUDENT";

        String token = jwtUtil.generateToken(userId, username, role);
        assertNotNull(token, "Token should not be null");
        System.out.println("Generated token: " + token);

        // 测试提取用户名
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername, "Username should match");

        // 测试提取用户ID
        Long extractedUserId = jwtUtil.extractUserId(token);
        assertEquals(userId, extractedUserId, "User ID should match");

        // 测试提取角色
        String extractedRole = jwtUtil.extractRole(token);
        assertEquals(role, extractedRole, "Role should match");

        // 测试验证令牌
        boolean isValid = jwtUtil.validateToken(token, username);
        assertTrue(isValid, "Token should be valid");
    }
}