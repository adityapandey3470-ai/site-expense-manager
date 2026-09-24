package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.ChangePasswordRequestDto;
import com.aditya.siteexpensemanager.dto.request.LoginRequestDto;
import com.aditya.siteexpensemanager.dto.request.RefreshTokenRequestDto;
import com.aditya.siteexpensemanager.dto.request.RegisterRequestDto;
import com.aditya.siteexpensemanager.dto.response.JwtResponseDto;
import com.aditya.siteexpensemanager.dto.response.UserResponseDto;
import com.aditya.siteexpensemanager.security.CustomUserDetails;
import com.aditya.siteexpensemanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(requestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto requestDto) {
        return ResponseEntity.ok(authService.refreshAccessToken(requestDto));
    }

    @Operation(summary = "Change your own password (requires current password)")
    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        authService.changePassword(currentUser.getUser().getId(), requestDto);
        return ResponseEntity.ok("Password changed successfully");
    }
}
