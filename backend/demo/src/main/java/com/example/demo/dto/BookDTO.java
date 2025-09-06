package com.example.demo.dto;

import com.example.library.entity.Book;

import java.time.LocalDateTime;

public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String description;
    private Long categoryId;
    private Integer likes; // 新增
    private String categoryName;
    private LocalDateTime createdAt;

    // 默认构造函数
    public BookDTO() {}

    // 从Book实体转换的构造函数
    public BookDTO(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.description = book.getDescription();
        this.likes = book.getLikes(); // 新增
        this.createdAt = book.getCreatedAt();

        if (book.getCategory() != null) {
            this.categoryId = book.getCategory().getId();
            this.categoryName = book.getCategory().getName();
        }
    }

    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getLikes() {return likes;}

    public void setLikes(Integer likes) {this.likes = likes;}
    // toString方法（可选，用于调试）
    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}