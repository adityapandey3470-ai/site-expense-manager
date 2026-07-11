package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.TravelExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelExpenseRepository
        extends JpaRepository<TravelExpense, Long> {

    List<TravelExpense> findAllByDeletedFalse();

    Optional<TravelExpense> findByIdAndDeletedFalse(Long id);

    boolean existsByTravelCode(String travelCode);
}