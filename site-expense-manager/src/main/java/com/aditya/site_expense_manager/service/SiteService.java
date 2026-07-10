package com.aditya.site_expense_manager.service;

import com.aditya.site_expense_manager.dto.request.SiteRequestDto;
import com.aditya.site_expense_manager.dto.response.SiteResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SiteService {
    SiteResponseDto createSite(SiteRequestDto requestDto);
    List<SiteResponseDto> getAllSites();
    SiteResponseDto getSiteById(Long id);
    SiteResponseDto updateSite(Long id, SiteRequestDto requestDto);
    void deleteSiteById(Long id);
    SiteResponseDto activateSite(Long id);
    SiteResponseDto deactivateSite(Long id);

}
