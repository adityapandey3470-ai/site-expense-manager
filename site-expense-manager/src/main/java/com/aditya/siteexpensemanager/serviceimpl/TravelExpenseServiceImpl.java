package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.request.TravelExpenseRequestDto;
import com.aditya.siteexpensemanager.dto.response.TravelExpenseResponseDto;
import com.aditya.siteexpensemanager.entity.Ledger;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.entity.TravelExpense;
import com.aditya.siteexpensemanager.enums.LedgerEntryType;
import com.aditya.siteexpensemanager.enums.LedgerSourceType;
import com.aditya.siteexpensemanager.enums.TravelExpenseStatus;
import com.aditya.siteexpensemanager.exception.ResourceNotFoundException;
import com.aditya.siteexpensemanager.mapper.TravelExpenseMapper;
import com.aditya.siteexpensemanager.repository.LedgerRepository;
import com.aditya.siteexpensemanager.repository.RequestRepository;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.repository.TravelExpenseRepository;
import com.aditya.siteexpensemanager.service.TravelExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TravelExpenseServiceImpl
        implements TravelExpenseService {

    private final TravelExpenseRepository travelExpenseRepository;
    private final SiteRepository siteRepository;
    private final TravelExpenseMapper travelExpenseMapper;
    private final RequestRepository requestRepository;
    private final LedgerRepository ledgerRepository;

    @Override
    public TravelExpenseResponseDto createTravelExpense(
            TravelExpenseRequestDto requestDto
    ) {

        Site site = siteRepository
                .findByIdAndDeletedFalse(requestDto.getSiteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                " Active Site not found with id: "
                                        + requestDto.getSiteId()
                        )
                );

        if (!site.getActive()) {
            throw new IllegalArgumentException(
                    "Travel expense cannot be created for inactive site"
            );
        }

        if (requestDto.getTravelDate().isBefore(site.getStartDate())
                || requestDto.getTravelDate().isAfter(site.getEndDate())) {

            throw new IllegalArgumentException(
                    "Travel date must be between site start date and end date"
            );
        }

        TravelExpense travelExpense =
                travelExpenseMapper.toEntity(requestDto, site);

        travelExpense.setTravelCode(generateTravelCode());
        travelExpense.setTravelStatus(
                TravelExpenseStatus.PENDING
        );
        travelExpense.setDeleted(false);

        TravelExpense savedTravelExpense =
                travelExpenseRepository.save(travelExpense);

        return travelExpenseMapper
                .toResponseDto(savedTravelExpense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TravelExpenseResponseDto>
    getAllTravelExpenses() {

        return travelExpenseRepository
                .findAllByDeletedFalseAndSiteDeletedFalse()
                .stream()
                .map(travelExpenseMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TravelExpenseResponseDto>
    getTravelExpensesBySiteId(Long siteId) {

        return travelExpenseRepository
                .findAllBySite_IdAndDeletedFalse(siteId)
                .stream()
                .map(travelExpenseMapper::toResponseDto)
                .toList();
    }

    @Override
    public TravelExpenseResponseDto
    getTravelExpenseById(Long id) {

        TravelExpense travelExpense =
                findActiveTravelExpenseById(id);

        return travelExpenseMapper
                .toResponseDto(travelExpense);
    }

    @Override
    public TravelExpenseResponseDto
    updateTravelExpenseById(
            Long id,
            TravelExpenseRequestDto requestDto
    ) {

        TravelExpense travelExpense =
                findActiveTravelExpenseById(id);

        if (travelExpense.getTravelStatus() != TravelExpenseStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending travel expense can be updated"
            );
        }

        Site site = siteRepository
                .findByIdAndDeletedFalse(requestDto.getSiteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: "
                                        + requestDto.getSiteId()
                        )
                );

        if (!site.getActive()) {
            throw new IllegalArgumentException(
                    "Travel expense cannot be assigned to inactive site"
            );
        }

        if (requestDto.getTravelDate().isBefore(site.getStartDate())
                || requestDto.getTravelDate().isAfter(site.getEndDate())) {

            throw new IllegalArgumentException(
                    "Travel date must be between site start date and end date"
            );
        }

        travelExpenseMapper.updateEntity(
                travelExpense,
                requestDto,
                site
        );

        TravelExpense updatedTravelExpense =
                travelExpenseRepository.save(travelExpense);

        return travelExpenseMapper
                .toResponseDto(updatedTravelExpense);
    }

    @Override
    public void softDeleteTravelExpenseById(Long id) {

        TravelExpense travelExpense =
                findActiveTravelExpenseById(id);

        if (requestRepository.existsByTravelExpense_IdAndDeletedFalse(id)) {
            throw new IllegalStateException(
                    "Travel expense cannot be deleted because it is linked to a request"
            );
        }

        if (ledgerRepository.existsBySourceTypeAndSourceIdAndDeletedFalse(
                LedgerSourceType.TRAVEL_EXPENSE,
                id
        ) || ledgerRepository.existsActiveRequestLedgerForTravelExpense(id)) {
            throw new IllegalStateException(
                    "Travel expense cannot be deleted because it is linked to a ledger entry"
            );
        }

        travelExpense.setDeleted(true);

        travelExpenseRepository.save(travelExpense);
    }

    @Override
    public void hardDeleteTravelExpenseById(Long id) {

        TravelExpense travelExpense =
                travelExpenseRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Travel expense not found with id: "
                                                + id
                                )
                        );

        if (requestRepository.existsByTravelExpense_Id(id)) {
            throw new IllegalStateException(
                    "Travel expense cannot be hard deleted because it is linked to a request"
            );
        }

        if (ledgerRepository.existsBySourceTypeAndSourceId(
                LedgerSourceType.TRAVEL_EXPENSE,
                id
        )) {
            throw new IllegalStateException(
                    "Travel expense cannot be hard deleted because it is linked to a ledger entry"
            );
        }

        travelExpenseRepository.delete(travelExpense);
    }


    @Override
    @Transactional
    public void markAsApproved(Long id) {

        TravelExpense travelExpense = findActiveTravelExpenseById(id);

        if (travelExpense.getTravelStatus() != TravelExpenseStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending travel expense can be approved"
            );
        }

        if (!Boolean.TRUE.equals(travelExpense.getBillAttached())) {
            throw new IllegalStateException(
                    "Travel expense cannot be approved without an attached invoice/bill"
            );
        }

        travelExpense.setTravelStatus(TravelExpenseStatus.APPROVED);
        travelExpenseRepository.save(travelExpense);

        postApprovedTravelExpenseToLedger(travelExpense);
    }

    private void postApprovedTravelExpenseToLedger(TravelExpense travelExpense) {

        if (ledgerRepository.existsBySourceTypeAndSourceIdAndDeletedFalse(
                LedgerSourceType.TRAVEL_EXPENSE, travelExpense.getId())) {
            return;
        }

        Ledger ledger = Ledger.builder()
                .site(travelExpense.getSite())
                .entryType(LedgerEntryType.DEBIT)
                .sourceType(LedgerSourceType.TRAVEL_EXPENSE)
                .sourceId(travelExpense.getId())
                .amount(travelExpense.getTravelCost())
                .description("Travel: " + travelExpense.getFromLocation()
                        + " -> " + travelExpense.getToLocation())
                .transactionDate(travelExpense.getTravelDate())
                .deleted(false)
                .build();

        ledgerRepository.save(ledger);
    }

    @Override
    public void markAsRejected(Long id) {

        TravelExpense travelExpense = findActiveTravelExpenseById(id);

        if (travelExpense.getTravelStatus() != TravelExpenseStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending travel expense can be rejected"
            );
        }

        travelExpense.setTravelStatus(TravelExpenseStatus.REJECTED);
        travelExpenseRepository.save(travelExpense);
    }

    private TravelExpense findActiveTravelExpenseById(
            Long id
    ) {

        return travelExpenseRepository
                .findByIdAndDeletedFalseAndSiteDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Travel expense not found with id: "
                                        + id
                        )
                );
    }

    private String generateTravelCode() {

        String travelCode;

        do {
            travelCode = "TRV-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        } while (
                travelExpenseRepository
                        .existsByTravelCode(travelCode)
        );

        return travelCode;
    }
}
