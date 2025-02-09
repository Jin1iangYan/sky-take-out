package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderReportDataDTO {

    // 当前日期
    private LocalDate date;

    // 订单完成率
    private Double orderCompletionRate;

    // 当前日期的订单数
    private Long orderCount;

    // 总订单数
    private Integer orderTotalCount;

    // 当前日期有效订单数（订单status为5）
    private Integer validOrderCount;

    // 总的有效订单数
    private Integer validOrderTotalCount;
}
