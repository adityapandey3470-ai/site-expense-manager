package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.request.LedgerRequestDto;
import com.aditya.siteexpensemanager.dto.response.LedgerResponseDto;

import java.util.List;

public interface LedgerService {

    LedgerResponseDto createLedger(LedgerRequestDto requestDto);

    List<LedgerResponseDto> getAllLedgers();

    LedgerResponseDto getLedgerById(Long ledgerId);

    List<LedgerResponseDto> getLedgersBySiteId(Long siteId);

    LedgerResponseDto updateLedger(Long ledgerId, LedgerRequestDto requestDto);

    void softDeleteLedger(Long ledgerId);

    void deleteLedger(Long ledgerId);
}
