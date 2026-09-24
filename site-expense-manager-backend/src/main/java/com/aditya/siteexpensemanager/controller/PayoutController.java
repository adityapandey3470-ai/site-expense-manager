package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.response.LedgerResponseDto;
import com.aditya.siteexpensemanager.dto.response.PayoutDueResponseDto;
import com.aditya.siteexpensemanager.service.PayoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payouts")
@Tag(name = "Payout APIs", description = "Mon/Wed/Fri disbursement cycle")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    @Operation(summary = "Get payout-due list for all active sites, most negative first")
    @GetMapping("/due")
    public ResponseEntity<List<PayoutDueResponseDto>> getPayoutDueList() {
        return ResponseEntity.ok(payoutService.getPayoutDueList());
    }

    @Operation(summary = "Get payout-due amount for a single site")
    @GetMapping("/due/site/{siteId}")
    public ResponseEntity<PayoutDueResponseDto> getPayoutDueForSite(@PathVariable Long siteId) {
        return ResponseEntity.ok(payoutService.getPayoutDueForSite(siteId));
    }

    @Operation(summary = "Mark a site as paid for this payout cycle (creates a CREDIT ledger entry)")
    @PostMapping("/site/{siteId}/pay")
    public ResponseEntity<LedgerResponseDto> markSitePaid(@PathVariable Long siteId) {
        return ResponseEntity.ok(payoutService.markSitePaid(siteId));
    }
}
