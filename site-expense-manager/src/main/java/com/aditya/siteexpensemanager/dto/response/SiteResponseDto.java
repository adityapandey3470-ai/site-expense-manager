package com.aditya.siteexpensemanager.dto.response;

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

public class SiteResponseDto {


    private Long id;
    private String siteName;
    private String siteCode;
    private String location;
    private String projectManager;
    private BigDecimal budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
}
