package com.aditya.siteexpensemanager.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AttendanceResponseDto {

    private Long id;
    private Long siteId;
    private String siteName;
    private LocalDate attendanceDate;
    private Integer presentCount;
    private BigDecimal foodRateApplied;
    private BigDecimal totalFoodAmount;
}
