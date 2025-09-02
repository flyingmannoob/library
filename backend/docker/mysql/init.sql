-- 使用 library_db 数据库
CREATE DATABASE IF NOT EXISTS library_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE library_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- 用户角色关联表（多对多关系）
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 图书表
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100),
    description TEXT,
    category_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 推荐记录表
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    book_id BIGINT,
    score DECIMAL(3,2), -- 推荐分 0.00 - 10.00
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- 初始化角色
INSERT INTO roles (role_name) VALUES ('ADMIN'), ('USER')
    ON DUPLICATE KEY UPDATE role_name=VALUES(role_name);

-- 初始化分类
INSERT INTO categories (name) VALUES ('Fiction'), ('Non-fiction'), ('Science'), ('History')
    ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 初始化用户（密码后续 Spring Security 会加密）
INSERT INTO users (username, password, email) VALUES
    ('admin', 'admin123', 'admin@example.com'),
    ('alice', 'alice123', 'alice@example.com');

-- 给 admin 分配 ADMIN 角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username='admin' AND r.role_name='ADMIN'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 给 alice 分配 USER 角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username='alice' AND r.role_name='USER'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 示例图书
INSERT INTO books (title, author, description, category_id) VALUES
    ('The Great Gatsby', 'F. Scott Fitzgerald', 'Classic American novel', 1),
    ('A Brief History of Time', 'Stephen Hawking', 'Cosmology explained', 3),
    ('Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', 'History of humankind', 4);
