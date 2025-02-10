package com.sky.service.impl;

import com.sky.dto.OrderReportDataDTO;
import com.sky.dto.TurnoverReportDataDTO;
import com.sky.dto.UserReportDataDTO;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    public WorkspaceServiceImpl(UserMapper userMapper, OrderMapper orderMapper) {
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
    }

    /**
     * 获取今日运营数据
     *
     * @return 今日运营数据
     */
    @Override
    public BusinessDataVO getBusinessData() {
        // 一天的开始于结束
        LocalDate today = LocalDate.now();
        LocalDateTime begin = today.atStartOfDay();
        LocalDateTime end = begin.plusDays(1).minusSeconds(1);

        // 获取新增用户数
        int newUser = 0;
        List<UserReportDataDTO> userReportDataDTOList = userMapper.selectUserReportByDateRange(begin, end);
        if (userReportDataDTOList != null && !userReportDataDTOList.isEmpty()) {
            newUser = userReportDataDTOList.get(0).getNewUserCount();
        }

        // 获取有效订单数和订单完成率
        int validOrderCount = 0;
        double validOrderRate = 0.0;
        List<OrderReportDataDTO> orderReportDataDTOList = orderMapper.selectOrderReportByDateRange(begin, end);
        if (orderReportDataDTOList != null && !orderReportDataDTOList.isEmpty()) {
            OrderReportDataDTO todayOrderReportDataDTO = orderReportDataDTOList.get(0);

            validOrderCount = todayOrderReportDataDTO.getValidOrderCount();
            validOrderRate = (double) validOrderCount / todayOrderReportDataDTO.getOrderCount();
        }

        // 获取营业额
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<TurnoverReportDataDTO> turnoverReportDataDTOList = orderMapper.selectTurnoverByDateRange(begin, end);
        if (turnoverReportDataDTOList != null && !turnoverReportDataDTOList.isEmpty()) {
            TurnoverReportDataDTO todayTurnoverReportDataDTO = turnoverReportDataDTOList.get(0);
            totalPrice = todayTurnoverReportDataDTO.getAmountSum();
        }

        // 获取平均客单价
        BigDecimal unitPrice = BigDecimal.ZERO;
        if (totalPrice.compareTo(BigDecimal.ZERO) > 0 && validOrderCount > 0) {
            unitPrice =  totalPrice.divide(BigDecimal.valueOf(validOrderCount), 2, RoundingMode.HALF_UP);
        }

        return BusinessDataVO.builder()
                .turnover(totalPrice)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(validOrderRate)
                .unitPrice(unitPrice)
                .newUsers(newUser)
                .build();
    }
}
