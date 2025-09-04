package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/vue-admin-template/book")
@CrossOrigin(origins = "http://localhost:9528", allowCredentials = "true")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private JwtUtil jwtUtil;

    // 获取所有图书（分页）
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long categoryId) {
        try {
            Pageable pageable = PageRequest.of(page - 1, limit);

            List<Book> books;
            long total;

            if (title != null || author != null || categoryId != null) {
                // 搜索模式
                books = bookService.searchBooks(title, author, categoryId);
                total = books.size();
                // 手动分页
                int start = (page - 1) * limit;
                int end = Math.min(start + limit, books.size());
                books = books.subList(start, end);
            } else {
                // 分页获取所有图书
                Page<Book> bookPage = bookService.getAllBooks(pageable);
                books = bookPage.getContent();
                total = bookPage.getTotalElements();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("items", books);
            data.put("total", total);

            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("获取图书列表失败: " + e.getMessage());
        }
    }

    // 根据ID获取图书详情
    @GetMapping("/{id}")
    public ApiResponse<Book> getBook(@PathVariable Long id) {
        try {
            Optional<Book> book = bookService.getBookById(id);
            if (book.isPresent()) {
                return ApiResponse.success(book.get());
            } else {
                return ApiResponse.error("图书不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("获取图书详情失败: " + e.getMessage());
        }
    }

    // 创建新图书（仅管理员）
    @PostMapping("/create")
    public ApiResponse<Book> createBook(@RequestBody Book book, @RequestHeader("X-Token") String token) {
        try {
            // 验证管理员权限
            if (!isAdmin(token)) {
                return ApiResponse.error("权限不足，只有管理员可以创建图书");
            }

            Book savedBook = bookService.saveBook(book);
            return ApiResponse.success(savedBook);
        } catch (Exception e) {
            return ApiResponse.error("创建图书失败: " + e.getMessage());
        }
    }

    // 更新图书（仅管理员）
    @PutMapping("/{id}")
    public ApiResponse<Book> updateBook(@PathVariable Long id, @RequestBody Book book, @RequestHeader("X-Token") String token) {
        try {
            // 验证管理员权限
            if (!isAdmin(token)) {
                return ApiResponse.error("权限不足，只有管理员可以修改图书");
            }

            Book updatedBook = bookService.updateBook(id, book);
            return ApiResponse.success(updatedBook);
        } catch (Exception e) {
            return ApiResponse.error("更新图书失败: " + e.getMessage());
        }
    }

    // 删除图书（仅管理员）
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBook(@PathVariable Long id, @RequestHeader("X-Token") String token) {
        try {
            // 验证管理员权限
            if (!isAdmin(token)) {
                return ApiResponse.error("权限不足，只有管理员可以删除图书");
            }

            bookService.deleteBook(id);
            return ApiResponse.success("图书删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除图书失败: " + e.getMessage());
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
