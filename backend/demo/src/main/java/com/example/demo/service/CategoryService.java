package com.example.demo.service;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 获取所有分类
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 根据ID获取分类
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // 根据名称获取分类
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    // 创建新分类
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // 删除分类
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
