package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.SystemSettingsRequestDto;
import com.aditya.siteexpensemanager.dto.response.SystemSettingsResponseDto;
import com.aditya.siteexpensemanager.service.SystemSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SystemSettingsController {

    private final SystemSettingsService systemSettingsService;

    @Operation(summary = "Get current system settings")
    @GetMapping
    public ResponseEntity<SystemSettingsResponseDto> getSettings() {
        return ResponseEntity.ok(systemSettingsService.getSettings());
    }

    @Operation(summary = "Update system settings (DIRECTOR only)")
    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<SystemSettingsResponseDto> updateSettings(
            @Valid @RequestBody SystemSettingsRequestDto requestDto
    ) {
        return ResponseEntity.ok(systemSettingsService.updateSettings(requestDto));
    }
}