package com.example.demo.dto;

import lombok.Data;

@Data
public class BookInfo {
    private Long id;
    private String title;
    private String author;
    private Long year;
    private String category;
    private Double rating;
}
