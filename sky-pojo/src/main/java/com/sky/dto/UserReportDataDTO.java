package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserReportDataDTO {
    // 日期
    private LocalDate date;
    // 该日期的新增用户数
    private Long newUserCount;
    // 该日期的总用户数
    private Long totalUserCount;
}
