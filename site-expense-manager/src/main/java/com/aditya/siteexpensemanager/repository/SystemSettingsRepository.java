package com.aditya.siteexpensemanager.repository;

import com.aditya.siteexpensemanager.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
}
