package com.example.demo.service;

import com.example.demo.dto.vo.BooksListVO;
import com.example.demo.dto.vo.BooksStatsVO;
import com.example.demo.dto.vo.CategoryStatsVO;
import com.example.demo.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    BooksListVO listBooks();
    BooksStatsVO statsBooks();
    List<CategoryStatsVO> statsCategory();
    Book findBookById(String id);
    List<Book> findBooksByKeyword(String keyword);
    List<Book> findBooksByCategoryId(Long categoryId);
    Long countAllBooks();
    Book saveBook(Book book);
    Boolean countBookById(Long id);
    void deleteBookById(Long id);
}
