package com.sky.service.impl;

import com.sky.dto.TurnoverDataDTO;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     *
     * @param begin 统计的开始时间
     * @param end 统计的结束时间
     * @return TurnoverReportVO
     */
    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        // 生成日期列表
        List<LocalDate> allDates = begin.datesUntil(end.plusDays(1)).collect(Collectors.toList());

        // 查询数据库获取营业额数据
        List<TurnoverDataDTO> turnoverDataList = orderMapper.selectTurnoverByDateRange(
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay());

        // 将营业额数据按日期存储到一个 Map 中，方便查找
        Map<LocalDate, BigDecimal> turnoverMap = turnoverDataList.stream()
                .collect(Collectors.toMap(TurnoverDataDTO::getOrderDate, TurnoverDataDTO::getAmountSum));

        // 初始化日期和营业额列表
        StringBuilder dateListStr = new StringBuilder();
        StringBuilder turnoverListStr = new StringBuilder();

        // 遍历日期范围
        for (LocalDate date : allDates) {
            // 日期列表
            if (dateListStr.length() > 0) {
                dateListStr.append(",");
            }
            dateListStr.append(date);

            // 获取该日期的营业额，如果没有数据则为 0
            BigDecimal amountSum = turnoverMap.getOrDefault(date, BigDecimal.ZERO);

            // 营业额列表
            if (turnoverListStr.length() > 0) {
                turnoverListStr.append(",");
            }
            turnoverListStr.append(amountSum);
        }

        // 创建并返回 TurnoverReportVO 对象
        return TurnoverReportVO.builder()
                .dateList(dateListStr.toString())
                .turnoverList(turnoverListStr.toString())
                .build();
    }
}
