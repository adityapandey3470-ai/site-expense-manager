package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.SiteRequestDto;
import com.aditya.siteexpensemanager.dto.response.SiteResponseDto;
import com.aditya.siteexpensemanager.service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sites")
@Tag(name = "Site APIs", description = "Operations for managing Sites")
public class SiteController {


    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @Operation(summary = "Create a new site")
    @PostMapping
    public ResponseEntity<SiteResponseDto> createSite(@Valid @RequestBody SiteRequestDto requestDto) {
        SiteResponseDto responseDto = siteService.createSite(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
}

    @Operation(summary = "Get all sites")
    @GetMapping
    public ResponseEntity<List<SiteResponseDto>> getAllSites() {
        List<SiteResponseDto> sites = siteService.getAllSites();
        return ResponseEntity.ok(sites);
    }

    @Operation(summary = "Get site by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SiteResponseDto> getSiteById(@PathVariable Long id){
        SiteResponseDto responseDto = siteService.getSiteById(id);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Update a site")
    @PutMapping("/{id}")
    public ResponseEntity<SiteResponseDto> updateSite(@PathVariable Long id,  @Valid @RequestBody SiteRequestDto requestDto) {
        SiteResponseDto responseDto = siteService.updateSite(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Soft delete a site by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSiteById(@PathVariable Long id){
        siteService.deleteSiteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Permanently delete a site by ID")
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteSiteById(@PathVariable Long id){
        siteService.hardDeleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activate a site")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<SiteResponseDto> activateSite(@PathVariable Long id){

        SiteResponseDto responseDto = siteService.activateSite(id);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Deactivate a site")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SiteResponseDto> deactivateSite(@PathVariable Long id){
        SiteResponseDto responseDto = siteService.deactivateSite(id);
        return ResponseEntity.ok(responseDto);
    }
}
