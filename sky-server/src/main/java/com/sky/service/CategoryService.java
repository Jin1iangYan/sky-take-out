package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 修改分类
     */
    void updateCategory(CategoryDTO category);

    /**
     * 分类分页查询
     */
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用/禁用分类
     */
    void updateStatus(Long categoryId, Integer status);

    /**
     * 新增分类
     */
    void addCategory(CategoryDTO category);

    /**
     * 根据id删除分类
     */
    void deleteCategory(Long categoryId);

    /**
     * 根据id查询分类
     *
     * @return
     */
    Category findById(Long categoryId);

    /**
     * 根据类型查询分类
     */
    List<Category> findByType(Integer type);
}
