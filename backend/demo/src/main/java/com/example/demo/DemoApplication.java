package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 数据库连接配置
        String url = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String username = "library_user";   // 替换为你的用户名
        String password = "libpass123";     // 替换为你的密码

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ 数据库连接成功！");
            } else {
                System.out.println("❌ 数据库连接失败！");
            }
        } catch (Exception e) {
            System.out.println("❌ 数据库连接失败: " + e.getMessage());
        }
    }
}
