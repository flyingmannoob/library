package com.example.demo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(com.example.library.DemoApplication.class, args);
    }
    @Bean
    public ApplicationRunner databaseConnectionChecker(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    System.out.println("✅ 数据库连接成功！");
                    System.out.println("数据库: " + conn.getMetaData().getDatabaseProductName());
                }
            } catch (Exception e) {
                System.out.println("❌ 数据库连接失败: " + e.getMessage());
            }
        };
    }
}
