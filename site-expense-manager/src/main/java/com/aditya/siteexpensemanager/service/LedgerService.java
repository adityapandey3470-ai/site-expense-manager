package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.request.LedgerRequestDto;
import com.aditya.siteexpensemanager.dto.response.LedgerResponseDto;
import com.aditya.siteexpensemanager.dto.response.SiteBalanceResponseDto;

import java.util.List;

public interface LedgerService {

    LedgerResponseDto createLedger(LedgerRequestDto requestDto);

    SiteBalanceResponseDto getSiteBalance(Long siteId);

    List<SiteBalanceResponseDto> getAllSiteBalances();

    List<LedgerResponseDto> getAllLedgers();

    LedgerResponseDto getLedgerById(Long ledgerId);

    List<LedgerResponseDto> getLedgersBySiteId(Long siteId);

    LedgerResponseDto updateLedger(Long ledgerId, LedgerRequestDto requestDto);

    void softDeleteLedger(Long ledgerId);

    void deleteLedger(Long ledgerId);
}
