package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.ConfirmActionRequestDto;
import com.aditya.siteexpensemanager.dto.request.RegisterRequestDto;
import com.aditya.siteexpensemanager.dto.request.ResetPasswordRequestDto;
import com.aditya.siteexpensemanager.dto.response.UserResponseDto;
import com.aditya.siteexpensemanager.security.CustomUserDetails;
import com.aditya.siteexpensemanager.service.AuthService;
import com.aditya.siteexpensemanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
public class AdminController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Create a user with any role (DIRECTOR only)")
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody RegisterRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerPrivileged(requestDto));
    }

    @Operation(summary = "List all users (DIRECTOR only)")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @Operation(summary = "Reset another user's password (DIRECTOR only)")
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequestDto requestDto
    ) {
        userService.resetPassword(id, requestDto);
        return ResponseEntity.ok("Password reset successfully");
    }

    @Operation(summary = "Activate or deactivate a user (DIRECTOR only). Deactivating a DIRECTOR requires re-entering your password.")
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<UserResponseDto> toggleActive(
            @PathVariable Long id,
            @RequestBody(required = false) ConfirmActionRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        String confirmPassword = requestDto != null ? requestDto.getConfirmPassword() : null;
        return ResponseEntity.ok(
                userService.toggleActive(id, currentUser.getUser().getId(), confirmPassword)
        );
    }

    @Operation(summary = "Delete a user (DIRECTOR only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        userService.deleteUser(id, currentUser.getUser().getId());
        return ResponseEntity.ok("User deleted successfully");
    }
}