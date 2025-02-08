package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
    /**
     * 插入一条订单
     * @param orders 订单对象
     */
    void insert(Orders orders);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Select("select * from orders where number = #{number}")
    Orders getByNumber(String number);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    Orders getById(Long id);

    Integer countStatus(Integer toBeConfirmed);
}
