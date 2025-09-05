package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {


    // 根据分类ID查找（不分页）
    List<Book> findByCategoryId(Long categoryId);

    // 分页查询
    Page<Book> findAll(Pageable pageable);

    // 搜索图书（标题或作者）
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author LIKE %:keyword%")
    List<Book> findByTitleContainingOrAuthorContaining(@Param("keyword") String keyword);
    // 新增：点赞操作
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.likes = b.likes + 1 WHERE b.id = :id")
    int incrementLikes(@Param("id") Long id);

    // 新增：取消点赞操作
    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.likes = b.likes - 1 WHERE b.id = :id AND b.likes > 0")
    int decrementLikes(@Param("id") Long id);

    // 新增：获取热门图书（按点赞数降序）
    @Query("SELECT b FROM Book b ORDER BY b.likes DESC")
    List<Book> findPopularBooks();

    // 新增：获取指定数量的热门图书
    @Query("SELECT b FROM Book b ORDER BY b.likes DESC")
    List<Book> findTopPopularBooks(Pageable pageable);
}