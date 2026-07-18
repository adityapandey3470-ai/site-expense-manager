package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.request.LedgerRequestDto;
import com.aditya.siteexpensemanager.dto.response.LedgerResponseDto;
import com.aditya.siteexpensemanager.dto.response.SiteBalanceResponseDto;
import com.aditya.siteexpensemanager.entity.Ledger;
import com.aditya.siteexpensemanager.entity.Request;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.entity.TravelExpense;
import com.aditya.siteexpensemanager.enums.LedgerSourceType;
import com.aditya.siteexpensemanager.enums.RequestStatus;
import com.aditya.siteexpensemanager.enums.RequestType;
import com.aditya.siteexpensemanager.enums.TravelExpenseStatus;
import com.aditya.siteexpensemanager.exception.ResourceNotFoundException;
import com.aditya.siteexpensemanager.mapper.LedgerMapper;
import com.aditya.siteexpensemanager.repository.LedgerRepository;
import com.aditya.siteexpensemanager.repository.RequestRepository;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.repository.TravelExpenseRepository;
import com.aditya.siteexpensemanager.service.LedgerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
public class LedgerServiceImpl implements LedgerService {

    private final LedgerRepository ledgerRepository;
    private final SiteRepository siteRepository;
    private final TravelExpenseRepository travelExpenseRepository;
    private final RequestRepository requestRepository;
    private final LedgerMapper ledgerMapper;

    public LedgerServiceImpl(
            LedgerRepository ledgerRepository,
            SiteRepository siteRepository,
            TravelExpenseRepository travelExpenseRepository,
            RequestRepository requestRepository,
            LedgerMapper ledgerMapper
    ) {
        this.ledgerRepository = ledgerRepository;
        this.siteRepository = siteRepository;
        this.travelExpenseRepository = travelExpenseRepository;
        this.requestRepository = requestRepository;
        this.ledgerMapper = ledgerMapper;
    }

    @Override
    @Transactional
    public LedgerResponseDto createLedger(LedgerRequestDto requestDto) {

        Site site = getValidActiveSite(requestDto.getSiteId());

        BigDecimal amount = resolveLedgerAmount(requestDto, site);

        validateUniqueLinkedSource(requestDto);

        Ledger ledger = ledgerMapper.toEntity(requestDto, site);
        ledger.setAmount(amount);

        Ledger savedLedger = ledgerRepository.save(ledger);

        return ledgerMapper.toResponseDto(savedLedger);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerResponseDto> getAllLedgers() {

        return ledgerRepository.findAllByDeletedFalseAndSiteDeletedFalse()
                .stream()
                .map(ledgerMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LedgerResponseDto getLedgerById(Long ledgerId) {

        Ledger ledger = getValidLedger(ledgerId);

        validateParentSiteNotDeleted(ledger);

        return ledgerMapper.toResponseDto(ledger);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerResponseDto> getLedgersBySiteId(Long siteId) {

        Site site = siteRepository.findByIdAndDeletedFalse(siteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: " + siteId
                        )
                );

        return ledgerRepository
                .findAllBySiteIdAndDeletedFalse(site.getId())
                .stream()
                .map(ledgerMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public LedgerResponseDto updateLedger(
            Long ledgerId,
            LedgerRequestDto requestDto
    ) {

        Ledger ledger = getValidLedger(ledgerId);

        Site site = getValidActiveSite(requestDto.getSiteId());

        BigDecimal amount = resolveLedgerAmount(requestDto, site);

        validateUniqueLinkedSourceForUpdate(requestDto, ledger.getLedgerId());

        ledgerMapper.updateEntity(ledger, requestDto, site);
        ledger.setAmount(amount);

        Ledger updatedLedger = ledgerRepository.save(ledger);

        return ledgerMapper.toResponseDto(updatedLedger);
    }

    @Override
    public void softDeleteLedger(Long ledgerId) {

        Ledger ledger = getValidLedger(ledgerId);

        ledger.setDeleted(true);

        ledgerRepository.save(ledger);
    }

    @Override
    public void deleteLedger(Long ledgerId) {

        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ledger not found with id: " + ledgerId
                        )
                );

        ledgerRepository.delete(ledger);
    }

    @Override
    @Transactional(readOnly = true)
    public SiteBalanceResponseDto getSiteBalance(Long siteId) {

        Site site = siteRepository.findByIdAndDeletedFalse(siteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: " + siteId
                        )
                );

        return toBalanceDto(site);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteBalanceResponseDto> getAllSiteBalances() {

        return siteRepository.findAllByDeletedFalse()
                .stream()
                .map(this::toBalanceDto)
                // Sites furthest in the negative appear first, matching the payout priority list.
                .sorted((a, b) -> a.getBalance().compareTo(b.getBalance()))
                .toList();
    }

    private SiteBalanceResponseDto toBalanceDto(Site site) {

        BigDecimal balance = ledgerRepository.getBalanceBySiteId(site.getId());

        return new SiteBalanceResponseDto(
                site.getId(),
                site.getSiteName(),
                site.getSiteCode(),
                site.getTeamSize(),
                balance,
                balance.signum() < 0
        );
    }

    private Ledger getValidLedger(Long ledgerId) {

        return ledgerRepository.findByLedgerIdAndDeletedFalse(ledgerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ledger not found with id: " + ledgerId
                        )
                );
    }

    private Site getValidActiveSite(Long siteId) {

        Site site = siteRepository.findByIdAndDeletedFalse(siteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: " + siteId
                        )
                );

        if (!site.getActive()) {
            throw new IllegalStateException(
                    "Ledger cannot be created for an inactive site"
            );
        }

        return site;
    }

    private void validateParentSiteNotDeleted(Ledger ledger) {

        if (ledger.getSite() == null || ledger.getSite().getDeleted()) {
            throw new ResourceNotFoundException(
                    "Parent site is deleted or unavailable"
            );
        }
    }

    private void validateUniqueLinkedSource(LedgerRequestDto requestDto) {

        if (requestDto.getSourceType() == LedgerSourceType.MANUAL) {
            return;
        }

        if (ledgerRepository.existsBySourceTypeAndSourceIdAndDeletedFalse(
                requestDto.getSourceType(),
                requestDto.getSourceId()
        )) {
            throw new IllegalStateException(
                    "Ledger already exists for this source"
            );
        }

        Long travelExpenseId = getLedgerTravelExpenseId(requestDto);

        if (travelExpenseId != null
                && hasLedgerForTravelExpense(travelExpenseId, null)) {
            throw new IllegalStateException(
                    "Ledger already exists for this travel expense"
            );
        }
    }

    private void validateUniqueLinkedSourceForUpdate(
            LedgerRequestDto requestDto,
            Long ledgerId
    ) {

        if (requestDto.getSourceType() == LedgerSourceType.MANUAL) {
            return;
        }

        if (ledgerRepository
                .existsBySourceTypeAndSourceIdAndDeletedFalseAndLedgerIdNot(
                        requestDto.getSourceType(),
                        requestDto.getSourceId(),
                        ledgerId
                )) {
            throw new IllegalStateException(
                    "Ledger already exists for this source"
            );
        }

        Long travelExpenseId = getLedgerTravelExpenseId(requestDto);

        if (travelExpenseId != null
                && hasLedgerForTravelExpense(travelExpenseId, ledgerId)) {
            throw new IllegalStateException(
                    "Ledger already exists for this travel expense"
            );
        }
    }

    private BigDecimal resolveLedgerAmount(
            LedgerRequestDto requestDto,
            Site site
    ) {

        if (requestDto.getSourceType() == null) {
            throw new IllegalArgumentException(
                    "Source type is required"
            );
        }

        if (requestDto.getSourceType() == LedgerSourceType.MANUAL) {

            if (requestDto.getSourceId() != null) {
                throw new IllegalArgumentException(
                        "Source id must be empty for manual ledger entry"
                );
            }

            if (requestDto.getAmount() == null) {
                throw new IllegalArgumentException(
                        "Amount is required for manual ledger entry"
                );
            }

            return requestDto.getAmount();
        }

        if (requestDto.getSourceId() == null) {
            throw new IllegalArgumentException(
                    "Source id is required for " + requestDto.getSourceType()
            );
        }

        if (requestDto.getSourceType()
                == LedgerSourceType.TRAVEL_EXPENSE) {

            TravelExpense travelExpense =
                    travelExpenseRepository
                            .findLockedByIdAndDeletedFalseAndSiteDeletedFalse(
                                    requestDto.getSourceId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Travel expense not found with id: "
                                                    + requestDto.getSourceId()
                                    )
                            );

            if (!travelExpense.getSite().getId().equals(site.getId())) {
                throw new IllegalArgumentException(
                        "Travel expense does not belong to the selected site"
                );
            }

            if (travelExpense.getTravelStatus() != TravelExpenseStatus.APPROVED) {
                throw new IllegalStateException(
                        "Only approved travel expense can be linked to a ledger entry"
                );
            }

            if (travelExpense.getTravelCost() == null) {
                throw new IllegalStateException(
                        "Approved travel expense does not have a source amount"
                );
            }

            return travelExpense.getTravelCost();
        }

        if (requestDto.getSourceType()
                == LedgerSourceType.REQUEST) {

            Request request =
                    requestRepository
                            .findLockedByIdAndDeletedFalseAndSiteDeletedFalse(
                                    requestDto.getSourceId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Request not found with id: "
                                                    + requestDto.getSourceId()
                                    )
                            );

            if (!request.getSite().getId().equals(site.getId())) {
                throw new IllegalArgumentException(
                        "Request does not belong to the selected site"
                );
            }
            if (request.getStatus() != RequestStatus.APPROVED) {
                throw new IllegalStateException(
                        "Only approved request can be linked to a ledger entry"
                );
            }

            if (request.getRequestType() != RequestType.TRAVEL_EXPENSE) {
                throw new IllegalStateException(
                        "Request is not linked to a travel expense and cannot be used as a ledger source"
                );
            }

            TravelExpense linkedTravelExpense = request.getTravelExpense();

            if (linkedTravelExpense == null) {
                throw new IllegalStateException(
                        "Request is not linked to a travel expense and cannot be used as a ledger source"
                );
            }

            Long linkedTravelExpenseId = linkedTravelExpense.getId();

            linkedTravelExpense = travelExpenseRepository
                    .findLockedByIdAndDeletedFalseAndSiteDeletedFalse(linkedTravelExpenseId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Linked travel expense not found with id: "
                                            + linkedTravelExpenseId
                            )
                    );



            if (!linkedTravelExpense.getSite().getId().equals(site.getId())) {
                throw new IllegalArgumentException(
                        "Linked travel expense does not belong to the selected site"
                );
            }

            if (linkedTravelExpense.getDeleted()
                    || linkedTravelExpense.getSite().getDeleted()) {
                throw new ResourceNotFoundException(
                        "Linked travel expense is deleted or unavailable"
                );
            }

            if (linkedTravelExpense.getTravelStatus()
                    != TravelExpenseStatus.APPROVED) {
                throw new IllegalStateException(
                        "Only approved linked travel expense can be used as a request ledger source"
                );
            }

            if (linkedTravelExpense.getTravelCost() == null) {
                throw new IllegalStateException(
                        "Approved linked travel expense does not have a source amount"
                );
            }

            return linkedTravelExpense.getTravelCost();
        }

        throw new IllegalArgumentException(
                "Unsupported ledger source type"
        );
    }

    private Long getLedgerTravelExpenseId(LedgerRequestDto requestDto) {

        if (requestDto.getSourceType() == LedgerSourceType.TRAVEL_EXPENSE) {
            return requestDto.getSourceId();
        }

        if (requestDto.getSourceType() == LedgerSourceType.REQUEST) {
            Request request = requestRepository
                    .findByIdAndDeletedFalseAndSiteDeletedFalse(
                            requestDto.getSourceId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Request not found with id: "
                                            + requestDto.getSourceId()
                            )
                    );

            if (request.getTravelExpense() == null) {
                return null;
            }

            return request.getTravelExpense().getId();
        }

        return null;
    }

    private boolean hasLedgerForTravelExpense(
            Long travelExpenseId,
            Long currentLedgerId
    ) {

        if (currentLedgerId == null) {
            return ledgerRepository
                    .existsBySourceTypeAndSourceIdAndDeletedFalse(
                            LedgerSourceType.TRAVEL_EXPENSE,
                            travelExpenseId
                    )
                    || ledgerRepository
                    .existsActiveRequestLedgerForTravelExpense(
                            travelExpenseId
                    );
        }

        return ledgerRepository
                .existsBySourceTypeAndSourceIdAndDeletedFalseAndLedgerIdNot(
                        LedgerSourceType.TRAVEL_EXPENSE,
                        travelExpenseId,
                        currentLedgerId
                )
                || ledgerRepository
                .existsActiveRequestLedgerForTravelExpenseAndLedgerIdNot(
                        travelExpenseId,
                        currentLedgerId
                );
    }
}
