package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        // 执行一个简单的SQL查询来测试数据库连接
        String sql = "SELECT VERSION()";
        String databaseVersion = jdbcTemplate.queryForObject(sql, String.class);
        
        // 验证查询结果不为空
        assertNotNull(databaseVersion);
        System.out.println("数据库版本: " + databaseVersion);
        
        // 验证数据库名称是否正确
        String dbNameSql = "SELECT DATABASE()";
        String databaseName = jdbcTemplate.queryForObject(dbNameSql, String.class);
        assertNotNull(databaseName);
        System.out.println("当前数据库: " + databaseName);
        assertTrue(databaseName.equals("school_club_platform"), "数据库名称不正确");
        
        System.out.println("数据库连接测试成功!");
    }
}
