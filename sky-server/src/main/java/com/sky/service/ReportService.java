package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计
     *
     * @param begin 统计的开始时间
     * @param end 统计的结束时间
     * @return TurnoverReportVO
     */
    TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end);
}
