package com.aditya.siteexpensemanager.dto.request;

import com.aditya.siteexpensemanager.enums.LedgerEntryType;
import com.aditya.siteexpensemanager.enums.LedgerSourceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerRequestDto {

    @NotNull(message = "Site id is required")
    @Positive(message = "Site id must be greater than zero")
    private Long siteId;

    @NotNull(message = "Entry type is required")
    private LedgerEntryType entryType;

    @NotNull(message = "Source type is required")
    private LedgerSourceType sourceType;

    @Positive(message = "Source id must be greater than zero")
    private Long sourceId;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;
}
