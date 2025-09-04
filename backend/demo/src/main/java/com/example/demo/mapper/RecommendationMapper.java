package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Recommendation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecommendationMapper extends BaseMapper<Recommendation> {
}
