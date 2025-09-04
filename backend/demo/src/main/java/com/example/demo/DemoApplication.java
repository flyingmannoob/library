package com.example.demo;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("✅ Spring Boot application started successfully!");
        System.out.println("✅ Server running on port");

        // 延迟初始化，避免启动时的数据库连接问题
        Thread.sleep(2000);

        try {
            userService.initDefaultUser();
            System.out.println("✅ Default user initialization completed! (admin/111111)");
        } catch (Exception e) {
            System.err.println("⚠️ Default user initialization failed, but server is running: " + e.getMessage());
        }
    }
}
