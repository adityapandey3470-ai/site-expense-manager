package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.TravelExpenseRequestDto;
import com.aditya.siteexpensemanager.dto.response.TravelExpenseResponseDto;
import com.aditya.siteexpensemanager.service.TravelExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-expenses")
@RequiredArgsConstructor
public class TravelExpenseController {

    private final TravelExpenseService travelExpenseService;

    @PostMapping
    public ResponseEntity<TravelExpenseResponseDto>
    createTravelExpense(
            @Valid
            @RequestBody
            TravelExpenseRequestDto requestDto
    ) {

        TravelExpenseResponseDto responseDto =
                travelExpenseService
                        .createTravelExpense(requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<TravelExpenseResponseDto>>
    getAllTravelExpenses() {

        return ResponseEntity.ok(
                travelExpenseService
                        .getAllTravelExpenses()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelExpenseResponseDto>
    getTravelExpenseById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                travelExpenseService
                        .getTravelExpenseById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelExpenseResponseDto>
    updateTravelExpenseById(
            @PathVariable Long id,

            @Valid
            @RequestBody
            TravelExpenseRequestDto requestDto
    ) {

        return ResponseEntity.ok(
                travelExpenseService
                        .updateTravelExpenseById(
                                id,
                                requestDto
                        )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    softDeleteTravelExpenseById(
            @PathVariable Long id
    ) {

        travelExpenseService
                .softDeleteTravelExpenseById(id);

        return ResponseEntity.ok(
                "Travel expense deleted successfully"
        );
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<String>
    hardDeleteTravelExpenseById(
            @PathVariable Long id
    ) {

        travelExpenseService
                .hardDeleteTravelExpenseById(id);

        return ResponseEntity.ok(
                "Travel expense permanently deleted successfully"
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<TravelExpenseResponseDto>
    approveTravelExpenseById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                travelExpenseService
                        .approveTravelExpenseById(id)
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<TravelExpenseResponseDto>
    rejectTravelExpenseById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                travelExpenseService
                        .rejectTravelExpenseById(id)
        );
    }
}