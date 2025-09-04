package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vue-admin-template")
@CrossOrigin(origins = "http://localhost:9528", allowCredentials = "true")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 获取所有分类
    @GetMapping("/categories")
    public ApiResponse<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.error("获取分类列表失败: " + e.getMessage());
        }
    }

    // 根据ID获取分类详情
    @GetMapping("/categories/{id}")
    public ApiResponse<Category> getCategory(@PathVariable Long id) {
        try {
            return categoryService.getCategoryById(id)
                    .map(ApiResponse::success)
                    .orElse(ApiResponse.error("分类不存在"));
        } catch (Exception e) {
            return ApiResponse.error("获取分类详情失败: " + e.getMessage());
        }
    }
}
