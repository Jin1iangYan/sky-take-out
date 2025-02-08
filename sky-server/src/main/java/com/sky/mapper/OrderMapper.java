package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    /**
     * 插入一条订单
     * @param orders 订单对象
     */
    void insert(Orders orders);
}
