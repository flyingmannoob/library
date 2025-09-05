package com.example.library.service.impl;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findBooksByKeyword(String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword);
    }


    // 新增：不分页的按分类查询方法
    @Override
    public List<Book> findBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public long countAllBooks() {
        return bookRepository.count();
    }
    // 新增：点赞方法
    @Override
    public boolean likeBook(Long id) {
        int updated = bookRepository.incrementLikes(id);
        return updated > 0;
    }

    // 新增：取消点赞方法
    @Override
    public boolean unlikeBook(Long id) {
        int updated = bookRepository.decrementLikes(id);
        return updated > 0;
    }

    // 新增：获取所有热门图书
    @Override
    public List<Book> getPopularBooks() {
        return bookRepository.findPopularBooks();
    }

    // 新增：获取指定数量的热门图书
    @Override
    public List<Book> getTopPopularBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookRepository.findTopPopularBooks(pageable);
    }
}