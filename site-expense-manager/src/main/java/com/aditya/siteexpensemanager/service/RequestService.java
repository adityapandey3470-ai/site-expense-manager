package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.request.RequestRequestDto;
import com.aditya.siteexpensemanager.dto.response.RequestResponseDto;

import java.util.List;

public interface RequestService {

    RequestResponseDto createRequest(RequestRequestDto requestDto);

    List<RequestResponseDto> getAllRequests();

    RequestResponseDto getRequestById(Long id);

    RequestResponseDto updateRequest(
            Long id,
            RequestRequestDto requestDto
    );

    void softDeleteRequest(Long id);

    void hardDeleteRequest(Long id);

    RequestResponseDto activateRequest(Long id);

    RequestResponseDto deactivateRequest(Long id);

    RequestResponseDto forwardRequest(Long id);

    RequestResponseDto approveRequest(
            Long id,
            String approverName
    );

    RequestResponseDto rejectRequest(
            Long id,
            String approverName,
            String rejectionReason
    );
}