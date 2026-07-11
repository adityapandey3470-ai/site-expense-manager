package com.aditya.siteexpensemanager.dto.request;

import com.aditya.siteexpensemanager.enums.TravelMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelExpenseRequestDto {

    @NotNull(message = "Site id is required")
    @Positive(message = "Site id must be greater than zero")
    private Long siteId;

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotBlank(message = "Employee id is required")
    private String employeeId;

    @NotNull(message = "Travel date is required")
    private LocalDate travelDate;

    @NotBlank(message = "From location is required")
    private String fromLocation;

    @NotBlank(message = "To location is required")
    private String toLocation;

    @NotNull(message = "Travel mode is required")
    private TravelMode travelMode;

    @NotNull(message = "Travel cost is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Travel cost must be greater than zero")
    private BigDecimal travelCost;

    @NotBlank(message = "Travel purpose is required")
    private String travelPurpose;

    private String remarks;
}