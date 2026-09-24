package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.LedgerRequestDto;
import com.aditya.siteexpensemanager.dto.response.LedgerResponseDto;
import com.aditya.siteexpensemanager.dto.response.SiteBalanceResponseDto;
import com.aditya.siteexpensemanager.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ledgers")
@Tag(name = "Ledger APIs", description = "Operations for managing ledger entries")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @Operation(summary = "Create a new ledger entry")
    @PostMapping
    public ResponseEntity<LedgerResponseDto> createLedger(
            @Valid @RequestBody LedgerRequestDto requestDto
    ) {
        LedgerResponseDto responseDto =
                ledgerService.createLedger(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @Operation(summary = "Get all ledger entries")
    @GetMapping
    public ResponseEntity<List<LedgerResponseDto>> getAllLedgers() {
        return ResponseEntity.ok(
                ledgerService.getAllLedgers()
        );
    }

    @Operation(summary = "Get ledger entry by ID")
    @GetMapping("/{ledgerId}")
    public ResponseEntity<LedgerResponseDto> getLedgerById(
            @PathVariable Long ledgerId
    ) {
        return ResponseEntity.ok(
                ledgerService.getLedgerById(ledgerId)
        );
    }

    @Operation(summary = "Get ledger entries by site ID")
    @GetMapping("/site/{siteId}")
    public ResponseEntity<List<LedgerResponseDto>> getLedgersBySiteId(
            @PathVariable Long siteId
    ) {
        return ResponseEntity.ok(
                ledgerService.getLedgersBySiteId(siteId)
        );
    }

    @Operation(summary = "Get live balance for a single site")
    @GetMapping("/balance/site/{siteId}")
    public ResponseEntity<SiteBalanceResponseDto> getSiteBalance(
            @PathVariable Long siteId
    ) {
        return ResponseEntity.ok(
                ledgerService.getSiteBalance(siteId)
        );
    }

    @Operation(summary = "Get live balance for every site, sorted most negative first")
    @GetMapping("/balance")
    public ResponseEntity<List<SiteBalanceResponseDto>> getAllSiteBalances() {
        return ResponseEntity.ok(
                ledgerService.getAllSiteBalances()
        );
    }

    @Operation(summary = "Update a ledger entry")
    @PutMapping("/{ledgerId}")
    public ResponseEntity<LedgerResponseDto> updateLedger(
            @PathVariable Long ledgerId,
            @Valid @RequestBody LedgerRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                ledgerService.updateLedger(ledgerId, requestDto)
        );
    }

    @Operation(summary = "Soft delete a ledger entry")
    @PatchMapping("/{ledgerId}")
    public ResponseEntity<String> softDeleteLedger(
            @PathVariable Long ledgerId
    ) {
        ledgerService.softDeleteLedger(ledgerId);

        return ResponseEntity.ok(
                "Ledger deleted successfully"
        );
    }

    @Operation(summary = "Permanently delete a ledger entry")
    @DeleteMapping("/{ledgerId}/hard")
    public ResponseEntity<String> deleteLedger(
            @PathVariable Long ledgerId
    ) {
        ledgerService.deleteLedger(ledgerId);

        return ResponseEntity.ok(
                "Ledger permanently deleted successfully"
        );
    }
}