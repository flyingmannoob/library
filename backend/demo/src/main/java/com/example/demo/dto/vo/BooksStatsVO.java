package com.example.demo.dto.vo;

import lombok.Data;

@Data
public class BooksStatsVO {
    private Long totalBooks;
    private Long latestYear;
    private Long authorCount;
    private Double averageRating;
}
