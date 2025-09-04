package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 根据标题搜索图书
    List<Book> findByTitleContainingIgnoreCase(String title);

    // 根据作者搜索图书
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // 根据分类ID搜索图书
    List<Book> findByCategoryId(Long categoryId);

    // 复合搜索
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:categoryId IS NULL OR b.categoryId = :categoryId)")
    List<Book> findBooksWithFilters(@Param("title") String title,
                                   @Param("author") String author,
                                   @Param("categoryId") Long categoryId);
}
