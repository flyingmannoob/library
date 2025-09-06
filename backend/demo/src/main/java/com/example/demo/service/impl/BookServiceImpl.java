package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.dto.BookInfo;
import com.example.demo.dto.vo.BooksListVO;
import com.example.demo.dto.vo.BooksStatsVO;
import com.example.demo.dto.vo.CategoryStatsVO;
import com.example.demo.entity.Book;
import com.example.demo.entity.Category;
import com.example.demo.mapper.BookMapper;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;

    Map<Long, String> NameCategory(){
        List<Category> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<>());
        Map<Long, String> result = new HashMap<Long, String>();
        categoryList.forEach((cat)->{result.put(cat.getId(), cat.getName());});
        return result;
    }

    @Override
    public BooksListVO listBooks(){
        Map<Long, String> catName = NameCategory();

        List<Book> bookList = bookMapper.selectList(new LambdaQueryWrapper<Book>()
                .select(Book::getId, Book::getTitle, Book::getAuthor,
                        Book::getCategoryId, Book::getCreatedAt, Book::getRating));

        List<BookInfo> booksInfo = bookList.stream().map((book)->{
            BookInfo bookInfo = new BookInfo();
            bookInfo.setId(book.getId());
            bookInfo.setTitle(book.getTitle());
            bookInfo.setAuthor(book.getAuthor());

            bookInfo.setCategory(catName.get(book.getCategoryId()));
            bookInfo.setYear(book.getCreatedAt().getYear() +1900L);
            bookInfo.setRating(book.getRating());
            return bookInfo;
        }).toList();

        BooksListVO vo = new BooksListVO();
        vo.setBooks(booksInfo);
        return vo;
    }

    @Override
    public BooksStatsVO statsBooks(){
        BooksStatsVO vo = new BooksStatsVO();
        Map<String, Object> parts = bookMapper.selectMaps(new QueryWrapper<Book>()
                .select("COUNT(id) as totBooks, COUNT(DISTINCT author) as cntAuth, AVG(rating) as avgRating"))
                .get(0);
        vo.setTotalBooks((Long) parts.get("totBooks"));
        vo.setLatestYear(bookMapper.selectOne(new LambdaQueryWrapper<Book>()
                .select(Book::getCreatedAt).orderByDesc(Book::getCreatedAt)
                .last("LIMIT 1")).getCreatedAt().getYear() +1900L);
        vo.setAuthorCount(((Long) parts.get("cntAuth")));
        vo.setAverageRating(((BigDecimal) parts.get("avgRating")).doubleValue());
        return vo;
    }

    @Override
    public List<CategoryStatsVO> statsCategory(){
        Map<Long, String> catName = NameCategory();
        List<Map<String, Object>> parts = bookMapper.selectMaps(new QueryWrapper<Book>()
                .select("category_id, COUNT(id) as cnt")
                .groupBy("category_id"));
        List<CategoryStatsVO> voList = parts.stream().map((pair)->{
            CategoryStatsVO vo = new CategoryStatsVO();
            vo.setName(catName.get((Long) pair.get("category_id")));
            vo.setValue((Long) pair.get("cnt"));
            return vo;
        }).toList();
        return voList;
    }
    @Override
    public Book findBookById(String id){
        return bookMapper.selectById(Long.parseLong(id));
    }
    @Override
    public List<Book> findBooksByKeyword(String keyword){
        return bookMapper.selectList(new LambdaQueryWrapper<Book>()
                .in(Book::getTitle, keyword).or().in(Book::getAuthor, keyword));
    }

    @Override
    public List<Book> findBooksByCategoryId(Long categoryId){
        return bookMapper.selectList(new LambdaQueryWrapper<Book>()
                .eq(Book::getCategoryId, categoryId));
    }

    @Override
    public Long countAllBooks(){
        return bookMapper.selectCount(new LambdaQueryWrapper<>());
    }

    @Override
    public Book saveBook(Book book){
        bookMapper.insert(book);
        return book;
    }

    @Override
    public Boolean countBookById(Long id){
        Book book = bookMapper.selectById(id);
        return book != null;
    }

    @Override
    public void deleteBookById(Long id){
        bookMapper.deleteById(id);
    }

    @Override
    public List<Book> searchBooks(String title, String author, Long categoryId) {
        LambdaQueryWrapper<Book> query = new LambdaQueryWrapper<Book>();
        if (title != null) query = query.eq(Book::getTitle, title);
        if (author != null) query = query.eq(Book::getAuthor, author);
        if (categoryId != null) query = query.eq(Book::getCategoryId, categoryId);
        List<Book> books = bookMapper.selectList(query);
        return books;
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        // 1. 构建MyBatis-Plus分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Book> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                        pageable.getPageNumber() + 1,
                        pageable.getPageSize()
                );

        // 2. 执行查询
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Book> mpResult =
                bookMapper.selectPage(mpPage, new LambdaQueryWrapper<Book>());

        // 3. 转换为Spring Data的Page
        List<Book> content = mpResult.getRecords();
        long total = mpResult.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Book updateBook(Long id, Book book){
        book.setId(id);
        bookMapper.updateById(book);
        return book;
    }
}
