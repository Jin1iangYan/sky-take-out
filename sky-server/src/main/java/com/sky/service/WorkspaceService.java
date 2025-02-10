package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkspaceService {

    /**
     * 获取日期范围内的运营数据
     * @return 运营数据
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询套餐总览
     * @return 套餐总览
     */
    SetmealOverViewVO getSetmealOverView();

    /**
     * 查询菜品总览
     * @return 菜品总览
     */
    DishOverViewVO getDishOverView();

    /**
     * 查询订单管理数据
     * @return 订单管理数据
     */
    OrderOverViewVO getOrderOverView();
}
