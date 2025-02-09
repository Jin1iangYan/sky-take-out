package com.sky.service.impl;

import com.sky.dto.OrderReportDataDTO;
import com.sky.dto.SalesTop10ReportDataDTO;
import com.sky.dto.TurnoverReportDataDTO;
import com.sky.dto.UserReportDataDTO;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WorkspaceService workspaceService;

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
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay().minusSeconds(1));

        // 将销售数据按日期存入 Map 中
        Map<LocalDate, BigDecimal> turnoverMap = turnoverDataList.stream()
                .collect(Collectors.toMap(
                        TurnoverReportDataDTO::getOrderDate,
                        TurnoverReportDataDTO::getAmountSum,
                        (existing, replacement) -> existing) // 如果日期重复，保留已有值
                );

        // 使用 StringBuilder 拼接结果
        StringBuilder dateListStr = new StringBuilder();
        StringBuilder turnoverListStr = new StringBuilder();

        for (LocalDate date : allDates) {
            if (dateListStr.length() > 0) {
                dateListStr.append(",");
                turnoverListStr.append(",");
            }
            dateListStr.append(date);

            // 获取该日期的对应营业额数据，若无数据则使用默认值
            BigDecimal turnover = turnoverMap.getOrDefault(date, BigDecimal.ZERO);
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
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay().minusSeconds(1));

        // 将用户数据按日期存入 Map 中
        Map<LocalDate, UserReportDataDTO> userMap = userDataList.stream()
                .collect(Collectors.toMap(
                        UserReportDataDTO::getDate,
                        data -> data,
                        (existing, replacement) -> existing) // 如果日期重复，保留已有值
                );

        StringBuilder dateListStr = new StringBuilder();
        StringBuilder newUserListStr = new StringBuilder();
        StringBuilder totalUserListStr = new StringBuilder();

        // 获取用户总数（截至统计开始日期）
        Long totalUserCount = userMapper.getUserCountBeforeDate(begin.atStartOfDay());
        assert totalUserCount != null;

        for (LocalDate date : allDates) {
            if (dateListStr.length() > 0) {
                dateListStr.append(",");
                newUserListStr.append(",");
                totalUserListStr.append(",");
            }
            dateListStr.append(date);

            // 获取新增用户数
            UserReportDataDTO userData = userMap.get(date);
            long newUserCount = (userData != null) ? userData.getNewUserCount() : 0L;
            // 同步用户总数
            if (newUserCount > 0) {
                totalUserCount += newUserCount;
            }

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
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay().minusSeconds(1));

        // 将订单数据按日期存入 Map 中
        Map<LocalDate, OrderReportDataDTO> orderMap = orderReportDateList.stream()
                .collect(Collectors.toMap(
                        OrderReportDataDTO::getDate,
                        data -> data,
                        (existing, replacement) -> existing) // 如果日期重复，保留已有值
                );

        StringBuilder orderCountListStr = new StringBuilder();
        StringBuilder validOrderCountListStr = new StringBuilder();

        int orderTotalCount = orderReportDateList.stream().mapToInt(OrderReportDataDTO::getOrderCount).sum();
        int validOrderTotalCount = orderReportDateList.stream().mapToInt(OrderReportDataDTO::getValidOrderCount).sum();
        double orderCompletionRate = validOrderTotalCount != 0 ? (double) validOrderTotalCount / orderTotalCount : 0.0;

        for (LocalDate date : allDates) {
            if (orderCountListStr.length() > 0) {
                orderCountListStr.append(",");
                validOrderCountListStr.append(",");
            }
            // 获取订单统计数据
            OrderReportDataDTO data = orderMap.get(date);
            if (data != null) {
                // 如果有数据，添加实际订单数量
                orderCountListStr.append(data.getOrderCount());
                validOrderCountListStr.append(data.getValidOrderCount());
            } else {
                // 如果没有数据，添加默认值 0
                orderCountListStr.append(0);
                validOrderCountListStr.append(0);
            }
        }

        return OrderReportVO.builder()
                .dateList(allDates.stream().map(LocalDate::toString).collect(Collectors.joining(",")))
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
                begin.atStartOfDay(), end.plusDays(1).atStartOfDay().minusSeconds(1));

        // 使用 Map 存储销售数据
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

    /**
     * 导出运营数据报表
     *
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(dateBegin.atStartOfDay(), dateEnd.atStartOfDay().minusSeconds(1));

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        assert in != null;

        try (
                // 通过模板文件创建 Excel 工作簿，同时自动关闭 in（如果需要的话，可以将 in 也加入到 try-with-resources 中）
                XSSFWorkbook excel = new XSSFWorkbook(in);
                // 从响应中获取输出流
                ServletOutputStream out = response.getOutputStream()
        ) {
            // 获取 Sheet 页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            // 填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

            // 获得第4行数据
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover().toString());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            // 获得第5行数据
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice().toString());

            // 填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                // 查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(
                        date.atStartOfDay(), date.plusDays(1).atStartOfDay().minusSeconds(1)
                );

                // 获取对应行，并填充数据
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover().toString());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice().toString());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            // 写入 Excel 数据到输出流，并刷新（flush）输出流
            excel.write(out);
            out.flush();
        } catch (IOException e) {
            log.error("生成报表失败: {}", e.getMessage());
        }

    }
}