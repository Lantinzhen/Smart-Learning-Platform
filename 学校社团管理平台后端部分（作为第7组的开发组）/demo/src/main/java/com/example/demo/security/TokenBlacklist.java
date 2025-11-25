package com.example.demo.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token黑名单管理类
 */
@Component
public class TokenBlacklist {
    
    // 使用线程安全的Set存储黑名单token
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();
    
    /**
     * 将token添加到黑名单
     * @param token JWT令牌
     */
    public void addToBlacklist(String token) {
        blacklist.add(token);
    }
    
    /**
     * 检查token是否在黑名单中
     * @param token JWT令牌
     * @return 是否在黑名单中
     */
    public boolean isInBlacklist(String token) {
        return blacklist.contains(token);
    }
    
    /**
     * 从黑名单中移除token（可选功能）
     * @param token JWT令牌
     */
    public void removeFromBlacklist(String token) {
        blacklist.remove(token);
    }
}