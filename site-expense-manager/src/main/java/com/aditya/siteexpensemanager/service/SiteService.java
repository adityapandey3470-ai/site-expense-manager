package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.request.SiteRequestDto;
import com.aditya.siteexpensemanager.dto.response.SiteResponseDto;

import java.util.List;


public interface SiteService {
    SiteResponseDto createSite(SiteRequestDto requestDto);
    List<SiteResponseDto> getAllSites();
    SiteResponseDto getSiteById(Long id);
    SiteResponseDto updateSite(Long id, SiteRequestDto requestDto);
    void deleteSiteById(Long id);
    void hardDeleteById(Long id);
    SiteResponseDto activateSite(Long id);
    SiteResponseDto deactivateSite(Long id);


}
