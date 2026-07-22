package com.aditya.siteexpensemanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponseDto {

    private BigDecimal totalDisbursed;
    private BigDecimal totalUsed;
    private long sitesInMinus;
    private long pendingApprovals;
}