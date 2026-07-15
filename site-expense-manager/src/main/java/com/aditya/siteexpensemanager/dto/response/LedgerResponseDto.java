package com.aditya.siteexpensemanager.dto.response;

import com.aditya.siteexpensemanager.enums.LedgerEntryType;
import com.aditya.siteexpensemanager.enums.LedgerSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerResponseDto {

    private Long ledgerId;

    private Long siteId;
    private String siteName;

    private LedgerEntryType entryType;

    private LedgerSourceType sourceType;

    private Long sourceId;

    private BigDecimal amount;

    private String description;

    private LocalDate transactionDate;
}