package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    List<Site> findAllByDeletedFalse();
    Optional<Site> findByIdAndDeletedFalse(Long aLong);
    boolean existsBySiteCode(String siteCode);
    boolean existsBySiteCodeAndIdNot(String siteCode, Long id);
}
