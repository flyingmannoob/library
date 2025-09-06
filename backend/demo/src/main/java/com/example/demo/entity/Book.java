package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
@Data
@TableName("books")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String author;
    private String description;
    @TableField(value = "category_id")
    private Long categoryId;
    @TableField(value = "created_at")
    private Timestamp createdAt;
    private Double rating;
    private Integer likes = 0; // 新增点赞数字段
}
