package com.aditya.siteexpensemanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


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
    private Double budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
}
