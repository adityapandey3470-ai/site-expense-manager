package com.aditya.siteexpensemanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

public class SiteRequestDto {


    @Size(min = 3, max = 100, message = "Site name must be between 3 and 100 characters")
    @NotBlank(message = "Site name is required")
    private String siteName;


    @Size(min = 3, max = 20, message = "Site code must be between 3 and 20 characters")
    @NotBlank(message = "Site code is required")
    private String siteCode;

    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    @NotBlank(message = "Location is required")
    private String location;

    @Size(min = 3, max = 100, message = "Project manager must be between 3 and 100 characters")
    @NotBlank(message = "Project manager is required")
    private String projectManager;

    @NotNull(message = "Budget is required")
    @Positive(message = "Budget must be a positive value")
    private BigDecimal budget;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Team size is required")
    @Positive(message = "Team size must be greater than zero")
    private Integer teamSize;
}
