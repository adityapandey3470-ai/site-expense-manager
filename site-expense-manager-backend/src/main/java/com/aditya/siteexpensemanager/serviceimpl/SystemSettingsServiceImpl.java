package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.config.SystemSettingsDefaultsProperties;
import com.aditya.siteexpensemanager.dto.request.SystemSettingsRequestDto;
import com.aditya.siteexpensemanager.dto.response.SystemSettingsResponseDto;
import com.aditya.siteexpensemanager.entity.SystemSettings;
import com.aditya.siteexpensemanager.repository.SystemSettingsRepository;
import com.aditya.siteexpensemanager.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SystemSettingsServiceImpl implements SystemSettingsService {

    private final SystemSettingsRepository systemSettingsRepository;
    private final SystemSettingsDefaultsProperties defaultsProperties;




    @Override
    @Transactional
    public SystemSettingsResponseDto getSettings() {
        return toResponseDto(getOrCreateSettings());
    }

    @Override
    @Transactional
    public SystemSettingsResponseDto updateSettings(SystemSettingsRequestDto requestDto) {

        SystemSettings settings = getOrCreateSettings();
        settings.setFoodRatePerPerson(requestDto.getFoodRatePerPerson());
        settings.setPayoutCycleDays(requestDto.getPayoutCycleDays());

        return toResponseDto(systemSettingsRepository.save(settings));
    }

    @Override
    @Transactional
    public BigDecimal getFoodRatePerPerson() {
        return getOrCreateSettings().getFoodRatePerPerson();
    }

    @Override
    @Transactional
    public int getPayoutCycleDays() {
        return getOrCreateSettings().getPayoutCycleDays();
    }

    private SystemSettings getOrCreateSettings() {
        return systemSettingsRepository.findById(1L)
                .orElseGet(() -> {
                    SystemSettings fresh = new SystemSettings();
                    fresh.setId(1L);
                    fresh.setFoodRatePerPerson(defaultsProperties.getFoodRatePerPerson());
                    fresh.setPayoutCycleDays(defaultsProperties.getPayoutCycleDays());
                    return systemSettingsRepository.save(fresh);
                });
    }

    private SystemSettingsResponseDto toResponseDto(SystemSettings settings) {
        return new SystemSettingsResponseDto(settings.getFoodRatePerPerson(), settings.getPayoutCycleDays());
    }
}