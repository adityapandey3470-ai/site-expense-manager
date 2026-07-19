package com.aditya.siteexpensemanager.controller;


import com.aditya.siteexpensemanager.dto.request.RequestRequestDto;
import com.aditya.siteexpensemanager.dto.response.RequestResponseDto;
import com.aditya.siteexpensemanager.security.CustomUserDetails;
import com.aditya.siteexpensemanager.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

@RestController
@RequestMapping("/requests")
@Tag(name = "Request APIs", description = "Operations for managing requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @Operation(summary = "Create a new request")
    @PostMapping
    public ResponseEntity<RequestResponseDto> createRequest(
            @Valid @RequestBody RequestRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        requestDto.setRequestedBy(currentUser.getUser().getFullName());

        RequestResponseDto responseDto =
                requestService.createRequest(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }


    @Operation(summary = "Get all requests")
    @GetMapping
    public ResponseEntity<List<RequestResponseDto>> getAllRequests() {

        return ResponseEntity.ok(
                requestService.getAllRequests()
        );
    }

    @Operation(summary = "Get all requests (paginated)")
    @GetMapping("/paged")
    public ResponseEntity<Page<RequestResponseDto>> getAllRequestsPaged(
            @PageableDefault(size = 5, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                requestService.getAllRequests(pageable)
        );
    }

    @Operation(summary = "Search requests with pagination (by description/requestedBy and status)")
    @GetMapping("/search")
    public ResponseEntity<Page<RequestResponseDto>> searchRequests(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) com.aditya.siteexpensemanager.enums.RequestStatus status,
            @PageableDefault(size = 5, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                requestService.searchRequests(search, status, pageable)
        );
    }

    @Operation(summary = "Get request by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDto> getRequestById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestService.getRequestById(id)
        );
    }


    @Operation(summary = "Update a request")
    @PutMapping("/{id}")
    public ResponseEntity<RequestResponseDto> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody RequestRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        requestDto.setRequestedBy(currentUser.getUser().getFullName());

        return ResponseEntity.ok(
                requestService.updateRequest(id, requestDto)
        );
    }

    @Operation(summary = "Soft delete a request")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> softDeleteRequest(
            @PathVariable Long id) {

        requestService.softDeleteRequest(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Hard delete a request")
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteRequest(
            @PathVariable Long id) {

        requestService.hardDeleteRequest(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activate a request")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<RequestResponseDto> activateRequest(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestService.activateRequest(id)
        );
    }

    @Operation(summary = "Deactivate a request")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<RequestResponseDto> deactivateRequest(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestService.deactivateRequest(id)
        );
    }

    @Operation(summary = "Forward an Emergency/Material request from Operations to Accounts+Director")
    @PatchMapping("/{id}/forward")
    @PreAuthorize("hasAnyAuthority('ROLE_OPERATIONS', 'ROLE_DIRECTOR')")
    public ResponseEntity<RequestResponseDto> forwardRequest(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.forwardRequest(id));
    }

    @Operation(summary = "Approve a request")
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_ACCOUNTS', 'ROLE_DIRECTOR')")
    public ResponseEntity<RequestResponseDto> approveRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        String approverName = currentUser.getUser().getFullName();

        return ResponseEntity.ok(
                requestService.approveRequest(id, approverName)
        );
    }

    @Operation(summary = "Reject a request")
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_OPERATIONS', 'ROLE_ACCOUNTS', 'ROLE_DIRECTOR')")
    public ResponseEntity<RequestResponseDto> rejectRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam String rejectionReason) {

        String approverName = currentUser.getUser().getFullName();

        return ResponseEntity.ok(
                requestService.rejectRequest(id, approverName, rejectionReason)
        );
    }
}