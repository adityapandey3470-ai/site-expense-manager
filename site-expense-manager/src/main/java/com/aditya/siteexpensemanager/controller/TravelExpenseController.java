package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.TravelExpenseRequestDto;
import com.aditya.siteexpensemanager.dto.response.TravelExpenseResponseDto;
import com.aditya.siteexpensemanager.service.TravelExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-expenses")
@Tag(name = "Travel Expense APIs", description = "Operations for managing travel expenses")
@RequiredArgsConstructor
public class TravelExpenseController {

    private final TravelExpenseService travelExpenseService;


    @Operation(summary = "Create a travel expense")
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

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Get all travel expenses")
    @GetMapping
    public ResponseEntity<List<TravelExpenseResponseDto>>
    getAllTravelExpenses() {

        return ResponseEntity.ok(
                travelExpenseService
                        .getAllTravelExpenses()
        );
    }

    @Operation(summary = "Get travel expense by ID")
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

    @Operation(summary = "Update travel expense")
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

    @Operation(summary = "Soft delete travel expense by ID")
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

    @Operation(summary = "Hard delete travel expense by ID")
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

  }