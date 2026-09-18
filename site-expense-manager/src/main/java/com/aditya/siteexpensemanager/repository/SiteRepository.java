package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.Site;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    List<Site> findAllByDeletedFalse();
    Optional<Site> findByIdAndDeletedFalse(Long aLong);
    boolean existsBySiteCode(String siteCode);
    boolean existsBySiteCodeAndIdNot(String siteCode, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Site s WHERE s.id = :id AND s.deleted = false")
    Optional<Site> findLockedByIdAndDeletedFalse(@Param("id") Long id);
}
