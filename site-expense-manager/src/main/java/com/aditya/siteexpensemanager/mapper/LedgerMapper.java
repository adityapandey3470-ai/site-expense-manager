package com.aditya.siteexpensemanager.mapper;

import com.aditya.siteexpensemanager.dto.request.LedgerRequestDto;
import com.aditya.siteexpensemanager.dto.response.LedgerResponseDto;
import com.aditya.siteexpensemanager.entity.Ledger;
import com.aditya.siteexpensemanager.entity.Site;
import org.springframework.stereotype.Component;

@Component
public class LedgerMapper {

    public Ledger toEntity(LedgerRequestDto requestDto, Site site) {

        Ledger ledger = new Ledger();

        ledger.setSite(site);
        ledger.setEntryType(requestDto.getEntryType());
        ledger.setSourceType(requestDto.getSourceType());
        ledger.setSourceId(requestDto.getSourceId());
        ledger.setAmount(requestDto.getAmount());
        ledger.setDescription(requestDto.getDescription());
        ledger.setTransactionDate(requestDto.getTransactionDate());
        ledger.setDeleted(false);

        return ledger;
    }

    public LedgerResponseDto toResponseDto(Ledger ledger) {

        LedgerResponseDto responseDto = new LedgerResponseDto();

        responseDto.setLedgerId(ledger.getLedgerId());
        responseDto.setSiteId(ledger.getSite().getId());
        responseDto.setSiteName(ledger.getSite().getSiteName());
        responseDto.setEntryType(ledger.getEntryType());
        responseDto.setSourceType(ledger.getSourceType());
        responseDto.setSourceId(ledger.getSourceId());
        responseDto.setAmount(ledger.getAmount());
        responseDto.setDescription(ledger.getDescription());
        responseDto.setTransactionDate(ledger.getTransactionDate());

        return responseDto;
    }

    public void updateEntity(
            Ledger ledger,
            LedgerRequestDto requestDto,
            Site site
    ) {

        ledger.setSite(site);
        ledger.setEntryType(requestDto.getEntryType());
        ledger.setSourceType(requestDto.getSourceType());
        ledger.setSourceId(requestDto.getSourceId());
        ledger.setAmount(requestDto.getAmount());
        ledger.setDescription(requestDto.getDescription());
        ledger.setTransactionDate(requestDto.getTransactionDate());
    }
}
