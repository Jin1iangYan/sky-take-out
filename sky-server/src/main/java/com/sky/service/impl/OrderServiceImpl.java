package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 提交订单
     *
     * @param ordersSubmitDTO 订单提交信息传输对象
     * @return 订单提交信息VO
     */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 1. 校验收货地址是否存在
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 2. 校验购物车数据是否为空
        Long currentUserId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectShoppingCart(
                ShoppingCart.builder().userId(currentUserId).build());
        if (shoppingCarts == null || shoppingCarts.isEmpty()) {
            throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 3. 创建订单对象，并将ordersSubmitDTO中属性复制到订单对象中
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setUserId(currentUserId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        // 生成订单号（UUID）
        orders.setNumber(UUID.randomUUID().toString().replace("-", ""));
        // 设置收货人信息
        // orders.setUserName();
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        // 拼接完整地址（假设 addressBook 有相应的字段，实际开发中可组合省、市、区、详细地址）
        orders.setAddress(addressBook.getDetail());

        // 4. 重新计算订单总金额，防止前端传入错误的金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ShoppingCart cart : shoppingCarts) {
            // 假设 ShoppingCart 中有 amount（单价）和 number（数量）字段
            BigDecimal itemTotal = cart.getAmount().multiply(new BigDecimal(cart.getNumber()));
            totalAmount = totalAmount.add(itemTotal);
        }
        orders.setAmount(totalAmount);

        // 5. 插入订单数据到订单表中（orderMapper.insert() 插入后会回填订单 id）
        orderMapper.insert(orders);

        // 6. 生成订单明细列表，并进行批量插入
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            // 直接复制购物车中的属性到订单明细中
            BeanUtils.copyProperties(cart, orderDetail);
            // 关联订单id
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);

        // 7. 清空当前用户的购物车数据
        shoppingCartMapper.deleteByUserId(currentUserId);

        // 8. 构造返回给前端的 VO 对象

        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }
}