package com.aditya.siteexpensemanager.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SystemSettingsRequestDto {

    @NotNull(message = "Food rate is required")
    @DecimalMin(value = "1", message = "Food rate must be greater than 0")
    private BigDecimal foodRatePerPerson;

    @NotNull(message = "Payout cycle days is required")
    @Min(value = 1, message = "Payout cycle days must be at least 1")
    private Integer payoutCycleDays;
}