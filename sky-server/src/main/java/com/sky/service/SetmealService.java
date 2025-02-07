package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 修改套餐
     */
    void updateSetmealWithSetmealDishes(SetmealDTO setmealDTO);

    /**
     * 分页查询
     */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 套餐起售、停售
     */
    void updateSetmealStatus(Long setmealId, Integer status);

    /**
     * 批量删除套餐
     */
    void deleteSetmealByIds(List<Long> ids);

    /**
     * 新增套餐
     */
    void addSetmealWithSetmealDishes(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐
     */
    SetmealVO findSetmealById(Long id);
    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
