package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.request.SiteRequestDto;
import com.aditya.siteexpensemanager.dto.response.SiteResponseDto;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.exception.ResourceNotFoundException;
import com.aditya.siteexpensemanager.mapper.SiteMapper;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.repository.TravelExpenseRepository;
import com.aditya.siteexpensemanager.service.SiteService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;
    private final TravelExpenseRepository travelExpenseRepository;



    public SiteServiceImpl(SiteRepository siteRepository, SiteMapper siteMapper, TravelExpenseRepository travelExpenseRepository) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.travelExpenseRepository = travelExpenseRepository;

    }

    private void validateSiteDates(SiteRequestDto requestDto) {
        if(requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    private void validateSiteCode(String siteCode) {
        if(siteRepository.existsBySiteCode(siteCode)) {
            throw new IllegalArgumentException("Site code already exists");
        }
    }

    private void validateSiteCodeForUpdate(String siteCode, Long id) {
        if(siteRepository.existsBySiteCodeAndIdNot(siteCode,id)) {
            throw new IllegalArgumentException("Site code already exists");
        }
    }

    private void validateExistingTravelExpenses(Long siteId, LocalDate startDate, LocalDate endDate) {
        var expenses = travelExpenseRepository.findAllBySite_IdAndDeletedFalse(siteId);

        boolean invalidExpenseExists = expenses.stream()
                .anyMatch(expense ->
                        expense.getTravelDate().isBefore(startDate)
                                || expense.getTravelDate().isAfter(endDate));

        if(invalidExpenseExists) {
            throw new IllegalArgumentException(
                    "site date range cannot be changed because existing travel expenses are outside the new date range");
        }
    }

        @Override
        public SiteResponseDto createSite (SiteRequestDto requestDto){
            validateSiteDates(requestDto);
            validateSiteCode(requestDto.getSiteCode());
            var site = siteMapper.toEntity(requestDto);
            var savedSite = siteRepository.save(site);
            return siteMapper.toResponseDto(savedSite);
        }

        @Override
    public List<SiteResponseDto> getAllSites() {
        var sites = siteRepository.findAllByDeletedFalse();
        return sites.stream()
                .map(siteMapper::toResponseDto)
                .toList();
        }

        @Override
        public SiteResponseDto getSiteById(Long id){
        var site = siteRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id " + id));
        return siteMapper.toResponseDto(site);
        }

        @Override
        public SiteResponseDto updateSite(Long id, SiteRequestDto requestDto){
        var site = siteRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id " + id));

        validateSiteDates(requestDto);
        validateSiteCodeForUpdate(requestDto.getSiteCode(), id);

        validateExistingTravelExpenses(
                id,
                requestDto.getStartDate(),
                requestDto.getEndDate()



        );

        siteMapper.updateEntityFromDto(requestDto, site);
        var savedSite = siteRepository.save(site);
        return siteMapper.toResponseDto(savedSite);
        }

        @Override
        public void deleteSiteById(Long id){
        var site = siteRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id " + id));

                  site.setDeleted(true);
                  site.setActive(false);
                   siteRepository.save(site);
        }

        @Override
        public SiteResponseDto activateSite(Long id){
        Site site = siteRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id " + id));
        site.setActive(true);
          Site updatedSite = siteRepository.save(site);
        return siteMapper.toResponseDto(updatedSite);
        }



        @Override
        public SiteResponseDto deactivateSite(Long id){
        Site site = siteRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id " + id));
        site.setActive(false);
        Site updatedSite = siteRepository.save(site);
        return siteMapper.toResponseDto(updatedSite);
        }
    }