package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.response.DashboardStatsResponseDto;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.enums.RequestStatus;
import com.aditya.siteexpensemanager.enums.TravelExpenseStatus;
import com.aditya.siteexpensemanager.repository.LedgerRepository;
import com.aditya.siteexpensemanager.repository.RequestRepository;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.repository.TravelExpenseRepository;
import com.aditya.siteexpensemanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SiteRepository siteRepository;
    private final LedgerRepository ledgerRepository;
    private final RequestRepository requestRepository;
    private final TravelExpenseRepository travelExpenseRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponseDto getDirectorDashboard() {

        BigDecimal totalDisbursed = ledgerRepository.getTotalCreditsAcrossSites();
        BigDecimal totalUsed = ledgerRepository.getTotalDebitsAcrossSites();

        long sitesInMinus = siteRepository.findAllByDeletedFalse()
                .stream()
                .filter(Site::getActive)
                .filter(site -> ledgerRepository.getBalanceBySiteId(site.getId()).signum() < 0)
                .count();

        long pendingRequests = requestRepository
                .countByStatusAndDeletedFalseAndSite_DeletedFalse(RequestStatus.PENDING);

        long pendingTravel = travelExpenseRepository
                .countByTravelStatusAndDeletedFalseAndSite_DeletedFalse(TravelExpenseStatus.PENDING);

        return new DashboardStatsResponseDto(
                totalDisbursed,
                totalUsed,
                sitesInMinus,
                pendingRequests + pendingTravel
        );
    }
}