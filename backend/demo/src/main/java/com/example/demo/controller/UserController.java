package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vue-admin-template/user")
@CrossOrigin(origins = "http://localhost:9528", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("收到登录请求 - 用户名: " + loginRequest.getUsername());
            String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            if (token != null) {
                System.out.println("登录成功，生成token: " + token.substring(0, Math.min(20, token.length())) + "...");
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                return ApiResponse.success(data);
            } else {
                System.out.println("登录失败 - 用户名或密码错误");
                return ApiResponse.error("用户名或密码错误");
            }
        } catch (Exception e) {
            System.err.println("登录异常: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getUserInfo(@RequestParam String token) {
        try {
            // 检查token是否为空或格式不正确
            if (token == null || token.trim().isEmpty()) {
                return ApiResponse.error("Token不能为空");
            }

            // 检查token是否是有效的JWT格式（至少包含两个点）
            if (token.split("\\.").length != 3) {
                return ApiResponse.error("Token格式无效");
            }

            String username = jwtUtil.extractUsername(token);
            if (username != null && jwtUtil.validateToken(token, username)) {
                User user = userService.findByUsername(username);
                if (user != null) {
                    Map<String, Object> data = new HashMap<>();
                    // 修复角色数据格式 - 前端期望的是字符串数组
                    String[] roles = {user.getRole()};
                    data.put("roles", roles);
                    data.put("name", user.getUsername()); // 使用用户名作为显示名称
                    data.put("avatar", user.getAvatar());

                    System.out.println("返回用户信息 - 用户名: " + user.getUsername() + ", 角色: " + user.getRole());
                    return ApiResponse.success(data);
                }
            }
            return ApiResponse.error("Token无效或用户不存在");
        } catch (Exception e) {
            System.err.println("获取用户信息错误: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        // 在实际应用中，可以将token加入黑名单
        return ApiResponse.success("退出成功");
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        try {
            System.out.println("收到注册请求 - 用户名: " + registerRequest.getUsername());

            // 验证输入参数
            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                return ApiResponse.error("用户名不能为空");
            }
            if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
                return ApiResponse.error("密码不能为空");
            }
            if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
                return ApiResponse.error("邮箱不能为空");
            }

            // 验证用户名长度
            if (registerRequest.getUsername().length() < 3 || registerRequest.getUsername().length() > 20) {
                return ApiResponse.error("用户名长度必须在3-20位之间");
            }

            // 验证密码长度
            if (registerRequest.getPassword().length() < 6) {
                return ApiResponse.error("密码长度至少6位");
            }

            // 简单的邮箱格式验证
            if (!registerRequest.getEmail().contains("@") || !registerRequest.getEmail().contains(".")) {
                return ApiResponse.error("邮箱格式不正确");
            }

            // 检查用户名是否已存在 - 修改错误信息
            if (userService.findByUsername(registerRequest.getUsername()) != null) {
                return ApiResponse.error("Please enter the correct user name");
            }

            // 调用用户服务进行注册
            User newUser = userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail()
            );

            System.out.println("用户注册成功: " + newUser.getUsername());

            // 注册成功后自动登录，生成token
            String token = userService.login(registerRequest.getUsername(), registerRequest.getPassword());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", newUser);

            return ApiResponse.success(data);

        } catch (Exception e) {
            System.err.println("注册异常: " + e.getMessage());
            // 如果是用户名已存在的异常，返回指定的错误信息
            if (e.getMessage().contains("用户名已存在")) {
                return ApiResponse.error("Please enter the correct user name");
            }
            return ApiResponse.error("注册失败: " + e.getMessage());
        }
    }
}
