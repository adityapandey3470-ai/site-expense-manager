package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.response.DashboardStatsResponseDto;
import com.aditya.siteexpensemanager.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard APIs", description = "Aggregated stats for the Director view")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get Director dashboard stats")
    @GetMapping("/director")
    @PreAuthorize("hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ACCOUNTS')")
    public ResponseEntity<DashboardStatsResponseDto> getDirectorDashboard() {

        return ResponseEntity.ok(dashboardService.getDirectorDashboard());
    }
}
