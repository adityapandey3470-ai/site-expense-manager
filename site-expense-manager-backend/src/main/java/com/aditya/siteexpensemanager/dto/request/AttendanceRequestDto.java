package com.aditya.siteexpensemanager.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AttendanceRequestDto {

    @NotNull(message = "Site id is required")
    @Positive(message = "Site id must be greater than zero")
    private Long siteId;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotNull(message = "Present count is required")
    @Positive(message = "Present count must be greater than zero")
    private Integer presentCount;
}
