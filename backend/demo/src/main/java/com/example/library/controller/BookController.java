package com.example.library.controller;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:9528")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDTO>> getBooks() {
        List<Book> books = bookService.findAllBooks();
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.findBookById(id);
        return book.map(b -> ResponseEntity.ok(new BookDTO(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String keyword) {
        List<Book> books = bookService.findBooksByKeyword(keyword);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BookDTO>> getBooksByCategory(@PathVariable Long categoryId) {
        List<Book> books = bookService.findBooksByCategoryId(categoryId);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    // 其他方法保持不变...
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookService.findBookById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getBookCount() {
        long count = bookService.countAllBooks();
        return ResponseEntity.ok(count);
    }
    // 新增：点赞接口
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeBook(@PathVariable Long id) {
        boolean success = bookService.likeBook(id);
        if (success) {
            Optional<Book> updatedBook = bookService.findBookById(id);
            return updatedBook.map(book -> ResponseEntity.ok(new BookDTO(book)))
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 新增：取消点赞接口
    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlikeBook(@PathVariable Long id) {
        boolean success = bookService.unlikeBook(id);
        if (success) {
            Optional<Book> updatedBook = bookService.findBookById(id);
            return updatedBook.map(book -> ResponseEntity.ok(new BookDTO(book)))
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.badRequest().body("无法取消点赞，点赞数可能已经为0");
        }
    }

    // 新增：获取热门图书列表
    @GetMapping("/popular")
    public ResponseEntity<List<BookDTO>> getPopularBooks() {
        List<Book> books = bookService.getPopularBooks();
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    // 新增：获取前N本热门图书
    @GetMapping("/popular/top")
    public ResponseEntity<List<BookDTO>> getTopPopularBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<Book> books = bookService.getTopPopularBooks(limit);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    // 新增：获取图书点赞数
    @GetMapping("/{id}/likes")
    public ResponseEntity<Integer> getBookLikes(@PathVariable Long id) {
        Optional<Book> book = bookService.findBookById(id);
        return book.map(b -> ResponseEntity.ok(b.getLikes()))
                .orElse(ResponseEntity.notFound().build());
    }

}