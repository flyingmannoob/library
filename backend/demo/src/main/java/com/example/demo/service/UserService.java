package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String username, String password) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (passwordEncoder.matches(password, user.getPassword())) {
                    return jwtUtil.generateToken(username);
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return null;
        }
    }

    public User findByUsername(String username) {
        try {
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            System.err.println("Find user error: " + e.getMessage());
            return null;
        }
    }

    public User register(String username, String password, String name) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");

        return userRepository.save(user);
    }

    public User registerUser(String username, String password, String email) {
        try {
            if (userRepository.existsByUsername(username)) {
                throw new RuntimeException("用户名已存在");
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);

            User savedUser = userRepository.save(user);
            System.out.println("新用户注册成功: " + savedUser.getUsername());
            return savedUser;
        } catch (Exception e) {
            System.err.println("注册用户失败: " + e.getMessage());
            throw new RuntimeException("注册失败: " + e.getMessage());
        }
    }

    public void initDefaultUser() {
        try {
            // 检查是否已有admin用户，如果没有则创建
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("111111"));
                admin.setEmail("admin@example.com");
                userRepository.save(admin);
                System.out.println("Default admin user created successfully (admin/111111)");
            } else {
                // 如果admin用户存在，检查密码是否需要更新为BCrypt格式
                Optional<User> adminOptional = userRepository.findByUsername("admin");
                if (adminOptional.isPresent()) {
                    User admin = adminOptional.get();
                    // 检查密码是否是BCrypt格式（BCrypt密码以$2a$开头）
                    if (!admin.getPassword().startsWith("$2a$") && !admin.getPassword().startsWith("$2b$")) {
                        // 如果不是BCrypt格式，更新为BCrypt格式
                        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
                        userRepository.save(admin);
                        System.out.println("Admin user password updated to BCrypt format");
                    }
                }
                System.out.println("Default admin user already exists");
            }
        } catch (Exception e) {
            System.err.println("Init default user error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            System.err.println("Get all users error: " + e.getMessage());
            throw new RuntimeException("获取用户列表失败");
        }
    }

    public User findById(Long id) {
        try {
            return userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Find user by id error: " + e.getMessage());
            return null;
        }
    }

    public User createUser(String username, String password, String name, String role) {
        try {
            if (userRepository.existsByUsername(username)) {
                throw new RuntimeException("用户名已存在");
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(name + "@example.com"); // 使用name作为邮箱前缀

            return userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Create user error: " + e.getMessage());
            throw new RuntimeException("创建用户失败: " + e.getMessage());
        }
    }

    public void deleteUser(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("用户不存在");
            }
            userRepository.deleteById(id);
        } catch (Exception e) {
            System.err.println("Delete user error: " + e.getMessage());
            throw new RuntimeException("删除用户失败: " + e.getMessage());
        }
    }

    public User updateUser(Long id, Map<String, String> userInfo) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            // 更新用户信息
            if (userInfo.containsKey("username")) {
                String newUsername = userInfo.get("username");
                // 检查新用户名是否已被其他用户使用
                if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
                    throw new RuntimeException("用户名已被使用");
                }
                user.setUsername(newUsername);
            }

            if (userInfo.containsKey("email")) {
                user.setEmail(userInfo.get("email"));
            }

            if (userInfo.containsKey("password") && !userInfo.get("password").isEmpty()) {
                user.setPassword(passwordEncoder.encode(userInfo.get("password")));
            }

            return userRepository.save(user);
        } catch (Exception e) {
            System.err.println("Update user error: " + e.getMessage());
            throw new RuntimeException("更新用户失败: " + e.getMessage());
        }
    }
}
