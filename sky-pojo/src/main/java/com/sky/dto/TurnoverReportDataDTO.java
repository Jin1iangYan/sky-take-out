package com.sky.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TurnoverReportDataDTO {
    private LocalDate orderDate;
    private BigDecimal amountSum;
}
