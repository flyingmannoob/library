package com.example.library.service;

import com.example.library.entity.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> findAllBooks();
    Optional<Book> findBookById(Long id);
    List<Book> findBooksByKeyword(String keyword);
    List<Book> findBooksByCategoryId(Long categoryId); // 新增：不分页的方法
    Book saveBook(Book book);
    void deleteBookById(Long id);
    long countAllBooks();
    // 新增：点赞相关方法
    boolean likeBook(Long id);
    boolean unlikeBook(Long id);
    List<Book> getPopularBooks();
    List<Book> getTopPopularBooks(int limit);
}