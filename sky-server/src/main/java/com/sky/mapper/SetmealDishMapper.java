package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据多个菜品id查询多个套餐id
     * @param dishIds
     * @return
     */
    // select setmeal_id from setmeal_dish where id in (...ids)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
