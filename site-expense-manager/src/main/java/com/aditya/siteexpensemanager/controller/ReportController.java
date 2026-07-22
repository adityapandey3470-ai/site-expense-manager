package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@Tag(name = "Report APIs", description = "Export data as downloadable reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Export ledger entries as CSV (all sites, or one site via siteId)")
    @GetMapping("/ledger/export")
    @PreAuthorize("hasAnyAuthority('ROLE_ACCOUNTS', 'ROLE_DIRECTOR')")
    public ResponseEntity<byte[]> exportLedgerCsv(
            @RequestParam(required = false) Long siteId
    ) {

        byte[] csv = reportService.exportLedgerCsv(siteId);

        String filename = (siteId != null)
                ? "ledger-site-" + siteId + ".csv"
                : "ledger-all-sites.csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @Operation(summary = "Export ledger entries as PDF (all sites, or one site via siteId)")
    @GetMapping("/ledger/export/pdf")
    @PreAuthorize("hasAnyAuthority('ROLE_ACCOUNTS', 'ROLE_DIRECTOR')")
    public ResponseEntity<byte[]> exportLedgerPdf(
            @RequestParam(required = false) Long siteId
    ) {

        byte[] pdf = reportService.exportLedgerPdf(siteId);

        String filename = (siteId != null)
                ? "ledger-site-" + siteId + ".pdf"
                : "ledger-all-sites.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
