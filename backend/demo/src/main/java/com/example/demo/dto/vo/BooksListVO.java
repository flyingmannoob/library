package com.example.demo.dto.vo;

import com.example.demo.dto.BookInfo;
import lombok.Data;

import java.util.List;
@Data
public class BooksListVO {
    private List<BookInfo> books;
}
