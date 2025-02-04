package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.ArrayList;
import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteDishesByIds(ArrayList<Long> ids);

    /**
     * 根据ID查询菜品
     * @param id
     * @return
     */
    DishVO findByIdWithFlavor(Long id);

    /**
     * 修改菜品
     * @param dishDTO 菜品DTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类查询菜品
     * @param categoryId 分类id
     * @return
     */
    List<Dish> findByCategoryId(Long categoryId);
}
