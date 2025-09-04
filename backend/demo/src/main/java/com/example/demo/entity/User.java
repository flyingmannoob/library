package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    // 在保存前自动设置创建时间
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    // 构造函数
    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 为了保持兼容性，添加一些便利方法
    public String getName() {
        return this.username; // 使用username作为name
    }

    public void setName(String name) {
        // 向后兼容，但实际不改变anything
    }

    public String getAvatar() {
        return "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"; // 默认头像
    }

    public void setAvatar(String avatar) {
        // 向后兼容，但实际不存储
    }

    public String getRole() {
        // 基于用户名判断角色
        if ("admin".equals(this.username)) {
            return "admin";
        }
        return "user"; // 默认用户角色
    }

    public void setRole(String role) {
        // 向后兼容，但实际不存储（角色基于用户名动态判断）
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
