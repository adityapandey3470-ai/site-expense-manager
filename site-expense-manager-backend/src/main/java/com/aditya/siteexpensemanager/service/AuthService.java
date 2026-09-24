package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.request.ChangePasswordRequestDto;
import com.aditya.siteexpensemanager.dto.request.LoginRequestDto;
import com.aditya.siteexpensemanager.dto.request.RefreshTokenRequestDto;
import com.aditya.siteexpensemanager.dto.request.RegisterRequestDto;
import com.aditya.siteexpensemanager.dto.response.JwtResponseDto;
import com.aditya.siteexpensemanager.dto.response.UserResponseDto;

public interface AuthService {

    UserResponseDto register(RegisterRequestDto requestDto);

    JwtResponseDto login(LoginRequestDto requestDto);

    UserResponseDto registerPrivileged(RegisterRequestDto requestDto);

    void changePassword(Long userId, ChangePasswordRequestDto requestDto);

    JwtResponseDto refreshAccessToken(RefreshTokenRequestDto requestDto);

}
