package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    void update(Category category);

    void insert(Category category);

    void delete(Long id);

    Category findById(Long id);

    Page<Category> pageList(CategoryPageQueryDTO categoryPageQueryDTO);

    List<Category> findByType(Integer type);
}
