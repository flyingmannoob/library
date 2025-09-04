package com.example.demo;

import com.example.demo.common.Result;
import com.example.demo.dto.vo.BooksListVO;
import com.example.demo.dto.vo.BooksStatsVO;
import com.example.demo.dto.vo.CategoryStatsVO;
import com.example.demo.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api") // 匹配任意前缀
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    /**
     * 获取书籍列表
     * @return 包含 code、data、message 的标准格式
     */
    @GetMapping("/books")
    public Result<BooksListVO> listBooks() {
        return Result.ok(bookService.listBooks());
    }

    @GetMapping("/books/stats")
    public Result<BooksStatsVO> statsBooks() {
        return Result.ok(bookService.statsBooks());
    }

    @GetMapping("/books/category")
    public Result<List<CategoryStatsVO>> statsCategory() {
        return Result.ok(bookService.statsCategory());
    }

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
