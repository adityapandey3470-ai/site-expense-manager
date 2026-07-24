package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.enums.ReportType;

import java.util.List;

public interface ReportService {

    byte[] exportCsv(ReportType type, List<Long> siteIds);

    byte[] exportPdf(ReportType type, List<Long> siteIds);
}