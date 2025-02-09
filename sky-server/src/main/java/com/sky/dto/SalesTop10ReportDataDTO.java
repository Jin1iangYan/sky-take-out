package com.sky.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SalesTop10ReportDataDTO {
    // 商品名称
    private String name;
    // 销售数量
    private Integer saleNumber;
}
