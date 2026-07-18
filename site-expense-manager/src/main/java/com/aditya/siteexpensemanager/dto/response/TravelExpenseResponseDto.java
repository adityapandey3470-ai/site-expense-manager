package com.aditya.siteexpensemanager.dto.response;

import com.aditya.siteexpensemanager.enums.TravelExpenseStatus;
import com.aditya.siteexpensemanager.enums.TravelMode;
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
public class TravelExpenseResponseDto {

    private Long id;

    private String travelCode;

    private Long siteId;

    private String siteName;

    private String employeeName;

    private String employeeId;

    private LocalDate travelDate;

    private String fromLocation;

    private String toLocation;

    private TravelMode travelMode;

    private BigDecimal travelCost;

    private String travelPurpose;

    private TravelExpenseStatus travelStatus;

    private String remarks;

    private Boolean billAttached;
}