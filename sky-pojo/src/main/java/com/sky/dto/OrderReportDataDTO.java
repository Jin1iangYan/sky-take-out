package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderReportDataDTO {

    // 当前日期
    private LocalDate date;

    // 当前日期的订单数
    private Integer orderCount;

    // 当前日期有效订单数（订单status为5）
    private Integer validOrderCount;
}
