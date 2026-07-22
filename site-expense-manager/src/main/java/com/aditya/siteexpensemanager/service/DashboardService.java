package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.response.DashboardStatsResponseDto;

public interface DashboardService {

    DashboardStatsResponseDto getDirectorDashboard();
}