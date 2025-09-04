package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vue-admin-template/admin")
@CrossOrigin(origins = "http://localhost:9528", allowCredentials = "true")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // 获取所有用户（仅管理员）
    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getAllUsers(@RequestHeader("X-Token") String token) {
        try {
            // 验证管理员权限
            if (!isAdmin(token)) {
                return ApiResponse.error("权限不足，只有管理员可以查看用户列表");
            }

            List<User> users = userService.getAllUsers();

            Map<String, Object> data = new HashMap<>();
            data.put("items", users);
            data.put("total", users.size());

            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }

    // 创建新用户（仅管理员）
    @PostMapping("/users")
    public ApiResponse<User> createUser(@RequestBody Map<String, String> userInfo, @RequestHeader("X-Token") String token) {
        try {
            // 验证管理员权限
            if (!isAdmin(token)) {
                return ApiResponse.error("权限不足，只有管理员可以创建用户");
            }

            String username = userInfo.get("username");
            String password = userInfo.get("password");
            String name = userInfo.get("name");
            String role = userInfo.get("role");

            if (username == null || password == null || name == null) {
                return ApiResponse.error("用户名、密码和姓名不能为空");
            }

            User user = userService.createUser(username, password, name, role);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error("创建用户失败: " + e.getMessage());
        }
    }

    // 删除用户（仅管理员）
    @DeleteMapping("/users/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id, @RequestHeader("X-Token") String token) {
        try {
            // 验证管理员权限
            if (!isAdmin(token)) {
                return ApiResponse.error("权限不足，只有管理员可以删除用户");
            }

            // 防止删除管理员自己
            String currentUsername = jwtUtil.extractUsername(token);
            User userToDelete = userService.findById(id);
            if (userToDelete != null && userToDelete.getUsername().equals(currentUsername)) {
                return ApiResponse.error("不能删除当前登录的管理员账户");
            }

            userService.deleteUser(id);
            return ApiResponse.success("用户删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除用户失败: " + e.getMessage());
        }
    }

    // 获取单个用户详情（仅管理员）
    @GetMapping("/users/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id, @RequestHeader("X-Token") String token) {
        try {
            // 验证管理员权限
            if (!isAdmin(token)) {
                return ApiResponse.error("权限不足，只有管理员可以查看用户详情");
            }

            User user = userService.findById(id);
            if (user != null) {
                return ApiResponse.success(user);
            } else {
                return ApiResponse.error("用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("获取用户详情失败: " + e.getMessage());
        }
    }

    // 更新用户信息（仅管理员）
    @PutMapping("/users/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody Map<String, String> userInfo, @RequestHeader("X-Token") String token) {
        try {
            // 验证管理员权限
            if (!isAdmin(token)) {
                return ApiResponse.error("权限不足，只有管理员可以更新用户信息");
            }

            User updatedUser = userService.updateUser(id, userInfo);
            return ApiResponse.success(updatedUser);
        } catch (Exception e) {
            return ApiResponse.error("更新用户失败: " + e.getMessage());
        }
    }

    // 验证用户是否为管理员
    private boolean isAdmin(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }

            String username = jwtUtil.extractUsername(token);
            if (username != null && jwtUtil.validateToken(token, username)) {
                // 管理员权限基于用户名判断
                return "admin".equals(username);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
