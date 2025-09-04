# 图书管理系统 API 接口文档

## 一、数据库表结构

项目包含以下6个核心数据表：

1. **users（用户表）**
2. **roles（角色表）**  
3. **user_roles（用户角色关联表）**
4. **categories（分类表）**
5. **books（图书表）**
6. **recommendations（推荐记录表）**

## 二、接口名与功能列表

### 用户管理接口
1. **POST /vue-admin-template/user/login** - 用户登录
2. **GET /vue-admin-template/user/info** - 获取用户信息  
3. **POST /vue-admin-template/user/logout** - 用户登出

### 图书管理接口
1. **GET /api/books** - 获取书籍列表

### 表格数据接口
1. **GET /vue-admin-template/table/list** - 获取表格数据列表

## 三、接口详细说明

### 1. 用户登录接口
- **接口名**: `POST /vue-admin-template/user/login`
- **功能**: 用户身份验证和登录
- **请求参数**:
  ```javascript
  {
    username: String,  // 用户名，必填
    password: String   // 密码，必填
  }
  ```
- **返回值字段**:
  ```javascript
  {
    code: Number,      // 状态码：20000-成功，60204-账号密码错误
    data: {
      token: String    // 认证令牌
    },
    message: String    // 错误消息（可选）
  }
  ```

### 2. 获取用户信息接口
- **接口名**: `GET /vue-admin-template/user/info`
- **功能**: 根据token获取用户详细信息
- **请求参数**:
  ```javascript
  {
    token: String      // 认证令牌，必填，通过query参数传递
  }
  ```
- **返回值字段**:
  ```javascript
  {
    code: Number,      // 状态码：20000-成功，50008-获取用户信息失败
    data: {
      roles: Array,           // 用户角色数组，如['admin', 'editor']
      introduction: String,   // 用户介绍
      avatar: String,         // 头像URL
      name: String           // 用户显示名称
    },
    message: String    // 错误消息（可选）
  }
  ```

### 3. 用户登出接口
- **接口名**: `POST /vue-admin-template/user/logout`
- **功能**: 用户登出
- **请求参数**: 无
- **返回值字段**:
  ```javascript
  {
    code: Number,      // 状态码：20000-成功
    data: String       // 返回"success"字符串
  }
  ```

### 4. 获取书籍列表接口
- **接口名**: `GET /api/books`
- **功能**: 获取图书馆书籍列表
- **请求参数**: 无
- **返回值字段**:
  ```javascript
  {
    code: Number,      // 状态码：20000-成功
    data: Array,       // 书籍数组，当前为简单字符串数组
    message: String    // 消息："success"
  }
  ```

### 5. 获取表格数据列表接口
- **接口名**: `GET /vue-admin-template/table/list`
- **功能**: 获取表格展示数据
- **请求参数**:
  ```javascript
  params: Object     // 查询参数对象（具体字段根据业务需求）
  ```
- **返回值字段**: 具体结构依赖于mock或后端实现

## 四、数据库表字段详细说明

### users表（用户表）
```sql
id: BIGINT AUTO_INCREMENT PRIMARY KEY    // 用户ID
username: VARCHAR(50) NOT NULL UNIQUE    // 用户名，唯一
password: VARCHAR(100) NOT NULL          // 密码（加密后）
email: VARCHAR(100)                      // 邮箱
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP  // 创建时间
```

### books表（图书表）
```sql
id: BIGINT AUTO_INCREMENT PRIMARY KEY    // 图书ID
title: VARCHAR(200) NOT NULL             // 书名
author: VARCHAR(100)                     // 作者
description: TEXT                        // 描述
category_id: BIGINT                      // 分类ID（外键）
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP  // 创建时间
```

### categories表（分类表）
```sql
id: BIGINT AUTO_INCREMENT PRIMARY KEY    // 分类ID
name: VARCHAR(50) NOT NULL UNIQUE        // 分类名称，唯一
```

### roles表（角色表）
```sql
id: BIGINT AUTO_INCREMENT PRIMARY KEY    // 角色ID
role_name: VARCHAR(50) NOT NULL UNIQUE   // 角色名称，唯一
```

### user_roles表（用户角色关联表）
```sql
user_id: BIGINT NOT NULL                 // 用户ID（外键）
role_id: BIGINT NOT NULL                 // 角色ID（外键）
PRIMARY KEY (user_id, role_id)           // 复合主键
```

### recommendations表（推荐记录表）
```sql
id: BIGINT AUTO_INCREMENT PRIMARY KEY    // 推荐ID
user_id: BIGINT                          // 用户ID（外键）
book_id: BIGINT                          // 图书ID（外键）
score: DECIMAL(3,2)                      // 推荐分数 0.00-10.00
reason: TEXT                             // 推荐理由
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP  // 创建时间
```

## 五、技术架构说明

- **前端框架**: Vue.js + Element UI
- **后端框架**: Spring Boot
- **数据库**: MySQL 8.0
- **API设计**: RESTful风格
- **认证方式**: Token认证
- **响应格式**: 统一JSON格式（包含code、data、message字段）

## 六、状态码说明

- **20000**: 请求成功
- **50008**: 登录失败，无法获取用户详细信息
- **60204**: 账号和密码不正确

## 七、注意事项

1. 所有接口返回值都遵循统一的响应格式
2. 用户密码在数据库中需要进行加密存储
3. Token认证用于用户身份验证和权限控制
4. 外键约束保证数据完整性
5. 支持用户多角色分配（多对多关系）
6. 图书分类支持层级管理
7. 推荐系统支持评分和理由记录

---
*本文档基于项目源码分析生成，如有变更请及时更新*
