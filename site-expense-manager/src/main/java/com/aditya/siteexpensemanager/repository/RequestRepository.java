package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.Request;
import com.aditya.siteexpensemanager.enums.RequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {


    Optional<Request> findByIdAndDeletedFalseAndSiteDeletedFalse(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select request
            from Request request
            where request.id = :id
              and request.deleted = false
              and request.site.deleted = false
            """)
    Optional<Request> findLockedByIdAndDeletedFalseAndSiteDeletedFalse(
            @Param("id") Long id
    );

    List<Request> findAllByDeletedFalseAndSiteDeletedFalse();

    boolean existsByRequestCode(String requestCode);

    boolean existsByTravelExpense_IdAndStatusAndDeletedFalseAndActiveTrue(
            Long travelExpenseId,
            RequestStatus status
    );

    boolean existsByTravelExpense_IdAndStatusAndDeletedFalseAndActiveTrueAndIdNot(
            Long travelExpenseId,
            RequestStatus status,
            Long id
    );

    boolean existsByTravelExpense_Id(Long travelExpenseId);
    boolean existsByTravelExpense_IdAndDeletedFalse(Long travelExpenseId);
    boolean existsBySite_Id(Long siteId);
}
