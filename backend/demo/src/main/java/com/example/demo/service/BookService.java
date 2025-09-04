package com.example.demo.service;

import com.example.demo.dto.vo.BooksListVO;
import com.example.demo.dto.vo.BooksStatsVO;
import com.example.demo.dto.vo.CategoryStatsVO;

import java.util.List;

public interface BookService {
    BooksListVO listBooks();
    BooksStatsVO statsBooks();
    List<CategoryStatsVO> statsCategory();
}
