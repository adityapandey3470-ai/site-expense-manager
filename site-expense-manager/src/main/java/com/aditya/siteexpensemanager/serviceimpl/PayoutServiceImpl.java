package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.response.LedgerResponseDto;
import com.aditya.siteexpensemanager.dto.response.PayoutDueResponseDto;
import com.aditya.siteexpensemanager.entity.Ledger;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.enums.LedgerEntryType;
import com.aditya.siteexpensemanager.enums.LedgerSourceType;
import com.aditya.siteexpensemanager.exception.ResourceNotFoundException;
import com.aditya.siteexpensemanager.mapper.LedgerMapper;
import com.aditya.siteexpensemanager.repository.LedgerRepository;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.service.PayoutService;
import com.aditya.siteexpensemanager.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {

    private final SiteRepository siteRepository;
    private final LedgerRepository ledgerRepository;
    private final LedgerMapper ledgerMapper;
    private final SystemSettingsService systemSettingsService;

    private static final Set<DayOfWeek> PAYOUT_DAYS = Set.of(
            DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY
    );

    @Override
    @Transactional(readOnly = true)
    public List<PayoutDueResponseDto> getPayoutDueList() {
        return siteRepository.findAllByDeletedFalse()
                .stream()
                .filter(Site::getActive)
                .map(this::toPayoutDueDto)
                .sorted((a, b) -> a.getCurrentBalance().compareTo(b.getCurrentBalance()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PayoutDueResponseDto getPayoutDueForSite(Long siteId) {
        return toPayoutDueDto(getActiveSite(siteId));
    }

    @Override
    @Transactional
    public LedgerResponseDto markSitePaid(Long siteId) {

        Site site = getActiveSiteLocked(siteId);

        if (!PAYOUT_DAYS.contains(LocalDate.now().getDayOfWeek())) {
            throw new IllegalStateException("Payouts can only be processed on Monday, Wednesday, or Friday.");
        }

        boolean alreadyPaidToday = ledgerRepository.existsBySite_IdAndSourceTypeAndTransactionDate(
                siteId, LedgerSourceType.PAYOUT, LocalDate.now());

        if (alreadyPaidToday) {
            throw new IllegalStateException("This site has already been marked paid today.");
        }

        BigDecimal amountDue = calculateAmountDue(site);

        if (amountDue.signum() <= 0) {
            throw new IllegalStateException("Nothing due for this site right now");
        }

        Ledger ledger = Ledger.builder()
                .site(site)
                .entryType(LedgerEntryType.CREDIT)
                .sourceType(LedgerSourceType.PAYOUT)
                .sourceId(site.getId())
                .amount(amountDue)
                .description("Payout disbursement (Mon/Wed/Fri cycle)")
                .transactionDate(LocalDate.now())
                .deleted(false)
                .build();

        Ledger savedLedger = ledgerRepository.save(ledger);
        return ledgerMapper.toResponseDto(savedLedger);
    }

    private Site getActiveSite(Long siteId) {
        Site site = siteRepository.findByIdAndDeletedFalse(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id: " + siteId));
        if (!site.getActive()) {
            throw new IllegalStateException("Site is inactive");
        }
        return site;
    }

    private PayoutDueResponseDto toPayoutDueDto(Site site) {
        BigDecimal balance = ledgerRepository.getBalanceBySiteId(site.getId());
        BigDecimal amountDue = calculateAmountDue(site, balance);

        boolean alreadyPaidToday = ledgerRepository.existsBySite_IdAndSourceTypeAndTransactionDate(
                site.getId(), LedgerSourceType.PAYOUT, LocalDate.now());
        boolean isPayoutDayToday = PAYOUT_DAYS.contains(LocalDate.now().getDayOfWeek());

        return new PayoutDueResponseDto(
                site.getId(), site.getSiteName(), site.getTeamSize(),
                balance, amountDue, alreadyPaidToday, isPayoutDayToday
        );
    }

    private BigDecimal calculateAmountDue(Site site) {
        return calculateAmountDue(site, ledgerRepository.getBalanceBySiteId(site.getId()));
    }

    private BigDecimal calculateAmountDue(Site site, BigDecimal balance) {

        BigDecimal foodRate = systemSettingsService.getFoodRatePerPerson();
        int cycleDays = systemSettingsService.getPayoutCycleDays();

        BigDecimal baseAdvance = foodRate
                .multiply(BigDecimal.valueOf(site.getTeamSize()))
                .multiply(BigDecimal.valueOf(cycleDays));


        return baseAdvance.subtract(balance);
    }

    private Site getActiveSiteLocked(Long siteId) {
        Site site = siteRepository.findLockedByIdAndDeletedFalse(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found with id: " + siteId));
        if (!site.getActive()) {
            throw new IllegalStateException("Site is inactive");
        }
        return site;
    }
}