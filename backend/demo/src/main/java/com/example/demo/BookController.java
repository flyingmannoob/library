package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/**") // 匹配任意前缀
public class BookController {

    /**
     * 获取书籍列表
     * @return 包含 code、data、message 的标准格式
     */
    @GetMapping("/books")
    public Map<String, Object> getBooks() {
        // 模拟书籍数据
        List<String> books = List.of("Book A", "Book B", "Book C");

        // 返回标准格式
        return Map.of(
                "code", 20000,
                "data", books,
                "message", "success"
        );
    }
}
