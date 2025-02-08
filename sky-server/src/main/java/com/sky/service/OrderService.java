package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 提交订单
     * @param ordersSubmitDTO 订单提交信息传输对象
     * @return 订单提交信息VO
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     *  查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult ordersPageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情（OrderVO）
     */
    OrderVO orderDetail(Long id);

    /**
     * 取消订单
     * @param id 订单id
     */
    void cancelOrder(Long id);

    /**
     * 再来一单
     * @param id 订单id
     */
    void repetition(Long id);
}
