package com.sky.service.impl;

import com.sky.dto.OrderReportDataDTO;
import com.sky.dto.SalesTop10ReportDataDTO;
import com.sky.dto.TurnoverReportDataDTO;
import com.sky.dto.UserReportDataDTO;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 营业额统计
     *
     * @param begin 统计的开始时间
     * @param end   统计的结束时间
     * @return TurnoverReportVO
     */
    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        List<LocalDate> allDates = begin.datesUntil(end.plusDays(1)).collect(Collectors.toList());
        List<TurnoverReportDataDTO> turnoverDataList = orderMapper.selectTurnoverByDateRange(
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay());

        StringBuilder dateListStr = new StringBuilder();
        StringBuilder turnoverListStr = new StringBuilder();

        for (LocalDate date : allDates) {
            if (dateListStr.length() > 0) {
                dateListStr.append(",");
                turnoverListStr.append(",");
            }
            dateListStr.append(date);

            // 获取该日期的对应营业额数据
            BigDecimal turnover = turnoverDataList.stream()
                    .filter(data -> data.getOrderDate().equals(date))
                    .map(TurnoverReportDataDTO::getAmountSum)
                    .findFirst()
                    .orElse(BigDecimal.ZERO);

            turnoverListStr.append(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(dateListStr.toString())
                .turnoverList(turnoverListStr.toString())
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin 统计的开始时间
     * @param end   统计的结束时间
     * @return UserReportVO
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> allDates = begin.datesUntil(end.plusDays(1)).collect(Collectors.toList());
        List<UserReportDataDTO> userDataList = userMapper.selectUserReportByDateRange(
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay());

        StringBuilder dateListStr = new StringBuilder();
        StringBuilder newUserListStr = new StringBuilder();
        StringBuilder totalUserListStr = new StringBuilder();

        for (LocalDate date : allDates) {
            if (dateListStr.length() > 0) {
                dateListStr.append(",");
                newUserListStr.append(",");
                totalUserListStr.append(",");
            }
            dateListStr.append(date);

            // 获取对应的新增用户数和总用户数
            Long newUserCount = userDataList.stream()
                    .filter(data -> data.getDate().equals(date))
                    .map(UserReportDataDTO::getNewUserCount)
                    .findFirst()
                    .orElse(0L);

            Long totalUserCount = userDataList.stream()
                    .filter(data -> data.getDate().equals(date))
                    .map(UserReportDataDTO::getTotalUserCount)
                    .findFirst()
                    .orElse(0L);

            newUserListStr.append(newUserCount);
            totalUserListStr.append(totalUserCount);
        }

        return UserReportVO.builder()
                .dateList(dateListStr.toString())
                .newUserList(newUserListStr.toString())
                .totalUserList(totalUserListStr.toString())
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin 统计的开始时间
     * @param end   统计的结束时间
     * @return OrderReportVO
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> allDates = begin.datesUntil(end.plusDays(1)).collect(Collectors.toList());
        List<OrderReportDataDTO> orderReportDateList = orderMapper.selectOrderReportByDateRange(
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay());

        StringBuilder orderCountListStr = new StringBuilder();
        StringBuilder validOrderCountListStr = new StringBuilder();

        Double orderCompletionRate = 0.0;
        Integer orderTotalCount = 0;
        Integer validOrderTotalCount = 0;

        for (LocalDate date : allDates) {
            if (orderCountListStr.length() > 0) {
                orderCountListStr.append(",");
                validOrderCountListStr.append(",");
            }
            orderCountListStr.append(0); // 默认订单数为 0
            validOrderCountListStr.append(0); // 默认有效订单数为 0

            // 获取订单统计数据
            OrderReportDataDTO data = orderReportDateList.stream()
                    .filter(order -> order.getDate().equals(date))
                    .findFirst()
                    .orElse(null);

            if (data != null) {
                orderCompletionRate = data.getOrderCompletionRate();
                orderTotalCount = data.getOrderTotalCount();
                validOrderTotalCount = data.getValidOrderTotalCount();
                orderCountListStr.append(data.getOrderCount());
                validOrderCountListStr.append(data.getValidOrderCount());
            }
        }

        return OrderReportVO.builder()
                .dateList(String.join(",", allDates.stream().map(LocalDate::toString).collect(Collectors.toList())))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(orderTotalCount)
                .orderCountList(orderCountListStr.toString())
                .validOrderCount(validOrderTotalCount)
                .validOrderCountList(validOrderCountListStr.toString())
                .build();
    }

    /**
     * 统计销售top10
     *
     * @param begin 统计的开始时间
     * @param end   统计的结束时间
     * @return SalesTop10ReportVO
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        List<SalesTop10ReportDataDTO> salesTop10ReportDateList = orderDetailMapper.selectSalesTop10ReportByDateRange(
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay());

        StringBuilder nameListStr = new StringBuilder();
        StringBuilder numberListStr = new StringBuilder();

        for (SalesTop10ReportDataDTO data : salesTop10ReportDateList) {
            if (nameListStr.length() > 0) {
                nameListStr.append(",");
                numberListStr.append(",");
            }
            nameListStr.append(data.getName());
            numberListStr.append(data.getSaleNumber());
        }

        return SalesTop10ReportVO.builder()
                .nameList(nameListStr.toString())
                .numberList(numberListStr.toString())
                .build();
    }
}