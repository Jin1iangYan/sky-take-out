package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 单条插入订单明细
     * @param orderDetail 订单详情明细
     */
    void insert(OrderDetail orderDetail);

    /**
     * 批量插入订单明细
     * @param orderDetails 订单详情明细列表
     */
    void insertBatch(List<OrderDetail> orderDetails);

    /**
     * 根据id查询订单明细
     * @param orderId 订单明细id
     * @return 订单明细
     */
    List<OrderDetail> getByOrderId(Long orderId);
}