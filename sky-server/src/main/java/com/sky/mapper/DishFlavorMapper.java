package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);


    /**
     * 根据多个菜品 ID 批量删除 dish_flavor 记录
     * @param dishIds 菜品 ID 列表
     */
    void deleteByDishIds(List<Long> dishIds);
}
