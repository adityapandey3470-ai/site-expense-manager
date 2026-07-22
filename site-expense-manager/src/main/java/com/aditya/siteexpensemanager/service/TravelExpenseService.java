package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.request.TravelExpenseRequestDto;
import com.aditya.siteexpensemanager.dto.response.TravelExpenseResponseDto;

import java.util.List;

public interface TravelExpenseService {

    TravelExpenseResponseDto createTravelExpense(
            TravelExpenseRequestDto requestDto
    );

    List<TravelExpenseResponseDto> getAllTravelExpenses();

    TravelExpenseResponseDto getTravelExpenseById(Long id);

    TravelExpenseResponseDto updateTravelExpenseById(
            Long id,
            TravelExpenseRequestDto requestDto
    );

    void softDeleteTravelExpenseById(Long id);

    void hardDeleteTravelExpenseById(Long id);

    void markAsApproved(Long id);
    void markAsRejected(Long id);
    List<TravelExpenseResponseDto> getTravelExpensesBySiteId(Long siteId);
}