package com.aditya.siteexpensemanager.controller;


import com.aditya.siteexpensemanager.dto.request.RequestRequestDto;
import com.aditya.siteexpensemanager.dto.response.RequestResponseDto;
import com.aditya.siteexpensemanager.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDto> createRequest(
            @Valid @RequestBody RequestRequestDto requestDto) {

        RequestResponseDto responseDto =
                requestService.createRequest(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<RequestResponseDto>> getAllRequests() {

        return ResponseEntity.ok(
                requestService.getAllRequests()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDto> getRequestById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestService.getRequestById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestResponseDto> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody RequestRequestDto requestDto) {

        return ResponseEntity.ok(
                requestService.updateRequest(id, requestDto)
        );
    }

    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<Void> softDeleteRequest(
            @PathVariable Long id) {

        requestService.softDeleteRequest(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteRequest(
            @PathVariable Long id) {

        requestService.hardDeleteRequest(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<RequestResponseDto> activateRequest(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestService.activateRequest(id)
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<RequestResponseDto> deactivateRequest(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestService.deactivateRequest(id)
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<RequestResponseDto> approveRequest(
            @PathVariable Long id,
            @RequestParam String approverName) {

        return ResponseEntity.ok(
                requestService.approveRequest(id, approverName)
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<RequestResponseDto> rejectRequest(
            @PathVariable Long id,
            @RequestParam String approverName,
            @RequestParam String rejectionReason) {

        return ResponseEntity.ok(
                requestService.rejectRequest(
                        id,
                        approverName,
                        rejectionReason
                )
        );
    }
}