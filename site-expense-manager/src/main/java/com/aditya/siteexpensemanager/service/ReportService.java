package com.aditya.siteexpensemanager.service;

public interface ReportService {

    byte[] exportLedgerCsv(Long siteId);
    byte[] exportLedgerPdf(Long siteId);
}