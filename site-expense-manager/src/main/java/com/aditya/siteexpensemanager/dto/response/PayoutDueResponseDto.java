package com.aditya.siteexpensemanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayoutDueResponseDto {

    private Long siteId;
    private String siteName;
    private Integer teamSize;
    private BigDecimal currentBalance;
    // Amount recommended for the next Mon/Wed/Fri disbursement:
    // 2 days of food advance for the team, plus covering any negative balance.
    private BigDecimal amountDue;
}
