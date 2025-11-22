package com.example.demo;

import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JpaTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testJpaConfig() {
        // 简单测试JPA配置是否正确加载
        System.out.println("UserRepository loaded: " + (userRepository != null));
    }
}
