package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.response.LedgerResponseDto;
import com.aditya.siteexpensemanager.dto.response.PayoutDueResponseDto;

import java.util.List;

public interface PayoutService {

    List<PayoutDueResponseDto> getPayoutDueList();

    PayoutDueResponseDto getPayoutDueForSite(Long siteId);

    LedgerResponseDto markSitePaid(Long siteId);
}
