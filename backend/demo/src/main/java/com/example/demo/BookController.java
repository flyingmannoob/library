package com.example.demo;

import com.example.demo.common.Result;
import com.example.demo.dto.vo.BooksListVO;
import com.example.demo.dto.vo.BooksStatsVO;
import com.example.demo.dto.vo.CategoryStatsVO;
import com.example.demo.service.BookService;
import com.example.demo.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/books/{id}")
    public Result<Book> getBookById(@PathVariable String id) {
        return Result.ok(bookService.findBookById(id));
    }

    @GetMapping("/books/search")
    public Result<List<Book>> searchBooks(@RequestParam String keyword){
        return Result.ok(bookService.findBooksByKeyword(keyword));
    }

    @GetMapping("/books/category/{categoryId}")
    public Result<List<Book>> getBooksByCategory(@PathVariable Long categoryId){
        return Result.ok(bookService.findBooksByCategoryId(categoryId));
    }

    @GetMapping("/books/count")
    public Result<Long> getBookCount() {
        long count = bookService.countAllBooks();
        return Result.ok(count);
    }
    @PostMapping("/books")
    public Result<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return Result.ok(savedBook);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        if (!bookService.countBookById(Long.parseLong(id))) {
            return ResponseEntity.notFound().build();
        }
        bookService.deleteBookById(Long.parseLong(id));
        return ResponseEntity.noContent().build();
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