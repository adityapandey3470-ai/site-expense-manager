package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.Request;
import com.aditya.siteexpensemanager.enums.RequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Page<Request> findAllByDeletedFalseAndSiteDeletedFalse(Pageable pageable);
    @Query("""
        select r from Request r
        where r.deleted = false
          and r.site.deleted = false
          and (:search is null or :search = ''
               or lower(r.description) like lower(concat('%', :search, '%'))
               or lower(r.requestedBy) like lower(concat('%', :search, '%')))
          and (:status is null or r.status = :status)
        """)
    Page<Request> searchRequests(
            @Param("search") String search,
            @Param("status") RequestStatus status,
            Pageable pageable
    );

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

    long countByStatusAndDeletedFalseAndSite_DeletedFalse(RequestStatus status);
}
