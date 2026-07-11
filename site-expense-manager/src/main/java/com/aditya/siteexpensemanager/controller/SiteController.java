package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.SiteRequestDto;
import com.aditya.siteexpensemanager.dto.response.SiteResponseDto;
import com.aditya.siteexpensemanager.service.SiteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sites")
public class SiteController {


    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }


    @PostMapping
    public ResponseEntity<SiteResponseDto> createSite(@Valid @RequestBody SiteRequestDto requestDto) {
        SiteResponseDto responseDto = siteService.createSite(requestDto);
        return  ResponseEntity.ok(responseDto);
}

    @GetMapping
    public ResponseEntity<List<SiteResponseDto>> getAllSites() {
        List<SiteResponseDto> sites = siteService.getAllSites();
        return ResponseEntity.ok(sites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteResponseDto> getSiteById(@PathVariable Long id){
        SiteResponseDto responseDto = siteService.getSiteById(id);
        return ResponseEntity.ok(responseDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<SiteResponseDto> updateSite(@PathVariable Long id,  @Valid @RequestBody SiteRequestDto requestDto) {
        SiteResponseDto responseDto = siteService.updateSite(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSiteById(@PathVariable Long id){
        siteService.deleteSiteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<SiteResponseDto> activateSite(@PathVariable Long id){

        SiteResponseDto responseDto = siteService.activateSite(id);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SiteResponseDto> deactivateSite(@PathVariable Long id){
        SiteResponseDto responseDto = siteService.deactivateSite(id);
        return ResponseEntity.ok(responseDto);
    }
}
