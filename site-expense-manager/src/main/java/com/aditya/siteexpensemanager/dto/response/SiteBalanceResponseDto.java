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
public class SiteBalanceResponseDto {

    private Long siteId;
    private String siteName;
    private String supervisorSiteCode;
    private Integer teamSize;
    private BigDecimal balance;
    // true when the team has spent more than what was disbursed (negative balance).
    private boolean inMinus;
}
