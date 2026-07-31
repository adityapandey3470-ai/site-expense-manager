package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.request.ResetPasswordRequestDto;
import com.aditya.siteexpensemanager.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> listUsers();

    void resetPassword(Long userId, ResetPasswordRequestDto requestDto);

    UserResponseDto toggleActive(Long targetUserId, Long actingUserId, String confirmPassword);
}