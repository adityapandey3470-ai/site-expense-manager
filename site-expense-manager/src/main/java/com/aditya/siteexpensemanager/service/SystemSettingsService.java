package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.request.SystemSettingsRequestDto;
import com.aditya.siteexpensemanager.dto.response.SystemSettingsResponseDto;

import java.math.BigDecimal;

public interface SystemSettingsService {

    SystemSettingsResponseDto getSettings();

    SystemSettingsResponseDto updateSettings(SystemSettingsRequestDto requestDto);

    BigDecimal getFoodRatePerPerson();

    int getPayoutCycleDays();
}
