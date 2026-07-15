package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.Ledger;
import com.aditya.siteexpensemanager.enums.LedgerSourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    Optional<Ledger> findByLedgerIdAndDeletedFalse(Long ledgerId);


    List<Ledger> findAllBySiteIdAndDeletedFalse(Long siteId);

    List<Ledger> findAllByDeletedFalseAndSiteDeletedFalse();

    boolean existsBySourceTypeAndSourceId(
            LedgerSourceType sourceType,
            Long sourceId
    );

    boolean existsBySourceTypeAndSourceIdAndDeletedFalse(
            LedgerSourceType sourceType,
            Long sourceId
    );

    boolean existsBySourceTypeAndSourceIdAndDeletedFalseAndLedgerIdNot(
            LedgerSourceType sourceType,
            Long sourceId,
            Long ledgerId
    );

    @Query("""
            select count(ledger) > 0
            from Ledger ledger, Request request
            where ledger.sourceType = com.aditya.siteexpensemanager.enums.LedgerSourceType.REQUEST
              and ledger.sourceId = request.id
              and request.travelExpense.id = :travelExpenseId
              and ledger.deleted = false
            """)
    boolean existsActiveRequestLedgerForTravelExpense(
            @Param("travelExpenseId") Long travelExpenseId
    );

    @Query("""
            select count(ledger) > 0
            from Ledger ledger, Request request
            where ledger.sourceType = com.aditya.siteexpensemanager.enums.LedgerSourceType.REQUEST
              and ledger.sourceId = request.id
              and request.travelExpense.id = :travelExpenseId
              and ledger.deleted = false
              and ledger.ledgerId <> :ledgerId
            """)
    boolean existsActiveRequestLedgerForTravelExpenseAndLedgerIdNot(
            @Param("travelExpenseId") Long travelExpenseId,
            @Param("ledgerId") Long ledgerId
    );

    boolean existsBySite_Id(Long siteId);

}
