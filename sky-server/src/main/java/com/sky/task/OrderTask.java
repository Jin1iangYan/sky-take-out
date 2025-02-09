package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeout() {
        log.info("定时处理超时订单: {}", LocalDateTime.now());

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 查询超时订单（超时定义为15分钟）
        List<Orders> timeoutOrders = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, now.minusMinutes(15));

        if (timeoutOrders == null || timeoutOrders.isEmpty()) {
            log.info("没有超时订单需要处理");
        } else {
            log.info("处理 {} 个超时订单", timeoutOrders.size());
            // 处理每个超时订单
            timeoutOrders.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(now);
                orderMapper.update(order);
            });
        }
    }

    /**
     * 处理一致处于派送中的订单，每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("处理一致处于派送中的订单: {}", LocalDateTime.now());

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        List<Orders> timeoutOrders = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, now.minusHours(1));

        if (timeoutOrders == null || timeoutOrders.isEmpty()) {
            log.info("没有派送订单需要处理");
        } else {
            log.info("处理 {} 个派送订单", timeoutOrders.size());
            // 处理每个超时订单
            timeoutOrders.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            });
        }
    }
}
