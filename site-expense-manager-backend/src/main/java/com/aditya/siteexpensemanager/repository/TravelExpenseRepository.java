package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.TravelExpense;
import com.aditya.siteexpensemanager.enums.TravelExpenseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TravelExpenseRepository
        extends JpaRepository<TravelExpense, Long> {

    List<TravelExpense> findAllByDeletedFalseAndSiteDeletedFalse();

    Optional<TravelExpense> findByIdAndDeletedFalseAndSiteDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select travelExpense
            from TravelExpense travelExpense
            where travelExpense.id = :id
              and travelExpense.deleted = false
              and travelExpense.site.deleted = false
            """)
    Optional<TravelExpense> findLockedByIdAndDeletedFalseAndSiteDeletedFalse(
            @Param("id") Long id
    );

    boolean existsByTravelCode(String travelCode);

    List<TravelExpense> findAllBySite_IdAndDeletedFalse(Long siteId);
    boolean existsBySite_Id(Long siteId);
    long countByTravelStatusAndDeletedFalseAndSite_DeletedFalse(TravelExpenseStatus travelStatus);
    List<TravelExpense> findAllBySite_IdInAndDeletedFalse(List<Long> siteIds);
}
