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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {

    private final SiteRepository siteRepository;
    private final LedgerRepository ledgerRepository;
    private final LedgerMapper ledgerMapper;

    @Value("${app.food-rate-per-person:330}")
    private BigDecimal foodRatePerPerson;

    // How many days of food advance to disburse on each Mon/Wed/Fri cycle.
    @Value("${app.payout-cycle-days:2}")
    private int payoutCycleDays;

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

        Site site = getActiveSite(siteId);

        BigDecimal amountDue = calculateAmountDue(site);

        if (amountDue.signum() <= 0) {
            throw new IllegalStateException(
                    "Nothing due for this site right now"
            );
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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: " + siteId
                        )
                );

        if (!site.getActive()) {
            throw new IllegalStateException(
                    "Site is inactive"
            );
        }

        return site;
    }

    private PayoutDueResponseDto toPayoutDueDto(Site site) {

        BigDecimal balance = ledgerRepository.getBalanceBySiteId(site.getId());
        BigDecimal amountDue = calculateAmountDue(site, balance);

        return new PayoutDueResponseDto(
                site.getId(),
                site.getSiteName(),
                site.getTeamSize(),
                balance,
                amountDue
        );
    }

    private BigDecimal calculateAmountDue(Site site) {
        return calculateAmountDue(site, ledgerRepository.getBalanceBySiteId(site.getId()));
    }

    private BigDecimal calculateAmountDue(Site site, BigDecimal balance) {

        // Base advance: N days of food for the whole team.
        BigDecimal baseAdvance = foodRatePerPerson
                .multiply(BigDecimal.valueOf(site.getTeamSize()))
                .multiply(BigDecimal.valueOf(payoutCycleDays));

        // If the site is already in the negative, cover that shortfall too.
        BigDecimal shortfallCover = balance.signum() < 0 ? balance.negate() : BigDecimal.ZERO;

        return baseAdvance.add(shortfallCover);
    }
}
