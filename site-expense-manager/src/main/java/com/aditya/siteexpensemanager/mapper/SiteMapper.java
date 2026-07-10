package com.aditya.siteexpensemanager.mapper;

import com.aditya.siteexpensemanager.dto.request.SiteRequestDto;
import com.aditya.siteexpensemanager.dto.response.SiteResponseDto;
import com.aditya.siteexpensemanager.entity.Site;
import org.springframework.stereotype.Component;

@Component
public class SiteMapper {
    public Site toEntity(SiteRequestDto requestDto) {
        Site site = new Site();

        site.setSiteName(requestDto.getSiteName());
        site.setSiteCode(requestDto.getSiteCode());
        site.setLocation(requestDto.getLocation());
        site.setProjectManager(requestDto.getProjectManager());
        site.setBudget(requestDto.getBudget());
        site.setStartDate(requestDto.getStartDate());
        site.setEndDate(requestDto.getEndDate());

        return site;
    }

    public SiteResponseDto toResponseDto(Site site) {
        SiteResponseDto responseDto = new SiteResponseDto();

        responseDto.setId(site.getId());
        responseDto.setSiteName(site.getSiteName());
        responseDto.setSiteCode(site.getSiteCode());
        responseDto.setLocation(site.getLocation());
        responseDto.setProjectManager(site.getProjectManager());
        responseDto.setBudget(site.getBudget());
        responseDto.setStartDate(site.getStartDate());
        responseDto.setEndDate(site.getEndDate());
        responseDto.setActive(site.getActive());

        return responseDto;
    }
    public void updateEntityFromDto(SiteRequestDto requestDto, Site site) {
        site.setSiteName(requestDto.getSiteName());
        site.setSiteCode(requestDto.getSiteCode());
        site.setLocation(requestDto.getLocation());
        site.setProjectManager(requestDto.getProjectManager());
        site.setBudget(requestDto.getBudget());
        site.setStartDate(requestDto.getStartDate());
        site.setEndDate(requestDto.getEndDate());
    }
}
