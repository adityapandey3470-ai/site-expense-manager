package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.enums.ReportType;
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

import java.util.List;

@RestController
@RequestMapping("/reports")
@Tag(name = "Report APIs", description = "Export data as downloadable reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Export a report as CSV — type = LEDGER | TRAVEL_EXPENSE | REQUEST")
    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyAuthority('ROLE_ACCOUNTS', 'ROLE_DIRECTOR')")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam ReportType type,
            @RequestParam(required = false) List<Long> siteIds
    ) {
        byte[] csv = reportService.exportCsv(type, siteIds);
        String filename = type.name().toLowerCase() + "-export.csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @Operation(summary = "Export a report as PDF — type = LEDGER | TRAVEL_EXPENSE | REQUEST")
    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyAuthority('ROLE_ACCOUNTS', 'ROLE_DIRECTOR')")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam ReportType type,
            @RequestParam(required = false) List<Long> siteIds
    ) {
        byte[] pdf = reportService.exportPdf(type, siteIds);
        String filename = type.name().toLowerCase() + "-export.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}