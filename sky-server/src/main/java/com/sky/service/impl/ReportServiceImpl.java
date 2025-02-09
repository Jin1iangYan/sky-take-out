package com.sky.service.impl;

import com.sky.dto.TurnoverReportDataDTO;
import com.sky.dto.UserReportDataDTO;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 生成日期和对应数据的逗号分隔字符串
     *
     * @param allDates 日期列表
     * @param dataList 数据列表
     * @param <T> 数据类型
     * @param <V> 数据值类型
     * @param dateExtractor 提取日期的函数
     * @param valueExtractor 提取值的函数
     * @return 包含日期和对应值的VO对象
     */
    private <T, V> String generateDateListAndValueList(
            List<LocalDate> allDates,
            List<T> dataList,
            java.util.function.Function<T, LocalDate> dateExtractor,
            java.util.function.Function<T, V> valueExtractor) {

        // 将数据按日期存储到一个 Map 中，方便查找
        Map<LocalDate, V> dataMap = dataList.stream()
                .collect(Collectors.toMap(dateExtractor, valueExtractor));

        // 初始化日期和对应值的字符串构建器
        StringBuilder dateListStr = new StringBuilder();
        StringBuilder valueListStr = new StringBuilder();

        // 遍历日期范围
        for (LocalDate date : allDates) {
            // 日期列表
            if (dateListStr.length() > 0) {
                dateListStr.append(",");
            }
            dateListStr.append(date);

            // 获取该日期的对应数据，如果没有数据则为默认值
            V value = dataMap.get(date);

            // 值列表
            if (valueListStr.length() > 0) {
                valueListStr.append(",");
            }
            valueListStr.append(value);
        }

        return dateListStr + "|" + valueListStr;
    }

    /**
     * 营业额统计
     *
     * @param begin 统计的开始时间
     * @param end   统计的结束时间
     * @return TurnoverReportVO
     */
    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        // 生成日期列表
        List<LocalDate> allDates = begin.datesUntil(end.plusDays(1)).collect(Collectors.toList());

        // 查询数据库获取营业额数据
        List<TurnoverReportDataDTO> turnoverDataList = orderMapper.selectTurnoverByDateRange(
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay());

        // 生成日期和营业额的逗号分隔字符串
        String[] result = generateDateListAndValueList(allDates, turnoverDataList,
                TurnoverReportDataDTO::getOrderDate, TurnoverReportDataDTO::getAmountSum).split("\\|");

        return TurnoverReportVO.builder()
                .dateList(result[0])
                .turnoverList(result[1])
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
        // 生成日期列表
        List<LocalDate> allDates = begin.datesUntil(end.plusDays(1)).collect(Collectors.toList());

        // 查询数据库获取用户统计数据
        List<UserReportDataDTO> userDataList = userMapper.selectUserReportByDateRange(
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay());

        // 生成日期和新增用户、总用户的逗号分隔字符串
        String[] result = generateDateListAndValueList(allDates, userDataList,
                UserReportDataDTO::getDate, UserReportDataDTO::getNewUserCount).split("\\|");
        String[] totalUserResult = generateDateListAndValueList(allDates, userDataList,
                UserReportDataDTO::getDate, UserReportDataDTO::getTotalUserCount).split("\\|");

        return UserReportVO.builder()
                .dateList(result[0])
                .newUserList(result[1])
                .totalUserList(totalUserResult[1])
                .build();
    }
}