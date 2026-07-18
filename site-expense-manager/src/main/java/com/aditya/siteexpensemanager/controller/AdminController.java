package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.RegisterRequestDto;
import com.aditya.siteexpensemanager.dto.response.UserResponseDto;
import com.aditya.siteexpensemanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @Operation(summary = "Create a user with any role (DIRECTOR only)")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody RegisterRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerPrivileged(requestDto));
    }
}
