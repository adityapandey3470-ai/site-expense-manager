package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {


    Optional<Request> findByIdAndDeletedFalseAndSiteDeletedFalse(Long id);

    List<Request> findAllByDeletedFalseAndSiteDeletedFalse();

    boolean existsByRequestCode(String requestCode);
}