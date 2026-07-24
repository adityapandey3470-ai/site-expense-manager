package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.entity.Ledger;
import com.aditya.siteexpensemanager.entity.Request;
import com.aditya.siteexpensemanager.entity.TravelExpense;
import com.aditya.siteexpensemanager.enums.LedgerEntryType;
import com.aditya.siteexpensemanager.enums.ReportType;
import com.aditya.siteexpensemanager.repository.LedgerRepository;
import com.aditya.siteexpensemanager.repository.RequestRepository;
import com.aditya.siteexpensemanager.repository.TravelExpenseRepository;
import com.aditya.siteexpensemanager.service.ReportService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final LedgerRepository ledgerRepository;
    private final TravelExpenseRepository travelExpenseRepository;
    private final RequestRepository requestRepository;



    @Override
    @Transactional(readOnly = true)
    public byte[] exportCsv(ReportType type, List<Long> siteIds) {
        return switch (type) {
            case LEDGER -> ledgerCsv(siteIds);
            case TRAVEL_EXPENSE -> travelExpenseCsv(siteIds);
            case REQUEST -> requestCsv(siteIds);
        };
    }

    private byte[] ledgerCsv(List<Long> siteIds) {

        List<Ledger> entries = (siteIds != null && !siteIds.isEmpty())
                ? ledgerRepository.findAllBySite_IdInAndDeletedFalse(siteIds)
                : ledgerRepository.findAllByDeletedFalseAndSiteDeletedFalse();

        entries = entries.stream().sorted(Comparator.comparing(Ledger::getTransactionDate)).toList();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println("Date,Site,Type,Source,Amount,Description");
            for (Ledger e : entries) {
                writer.println(
                        e.getTransactionDate() + "," + escape(e.getSite().getSiteName()) + ","
                                + e.getEntryType() + "," + e.getSourceType() + "," + e.getAmount() + ","
                                + escape(e.getDescription())
                );
            }
        }
        return out.toByteArray();
    }

    private byte[] travelExpenseCsv(List<Long> siteIds) {

        List<TravelExpense> entries = (siteIds != null && !siteIds.isEmpty())
                ? travelExpenseRepository.findAllBySite_IdInAndDeletedFalse(siteIds)
                : travelExpenseRepository.findAllByDeletedFalseAndSiteDeletedFalse();

        entries = entries.stream().sorted(Comparator.comparing(TravelExpense::getTravelDate)).toList();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println("Date,Site,Employee,From,To,Mode,Cost,Purpose,Status,Bill Attached");
            for (TravelExpense e : entries) {
                writer.println(
                        e.getTravelDate() + "," + escape(e.getSite().getSiteName()) + "," + escape(e.getEmployeeName()) + ","
                                + escape(e.getFromLocation()) + "," + escape(e.getToLocation()) + "," + e.getTravelMode() + ","
                                + e.getTravelCost() + "," + escape(e.getTravelPurpose()) + "," + e.getTravelStatus() + ","
                                + (Boolean.TRUE.equals(e.getBillAttached()) ? "Yes" : "No")
                );
            }
        }
        return out.toByteArray();
    }

    private byte[] requestCsv(List<Long> siteIds) {

        List<Request> entries = (siteIds != null && !siteIds.isEmpty())
                ? requestRepository.findAllBySite_IdInAndDeletedFalse(siteIds)
                : requestRepository.findAllByDeletedFalseAndSiteDeletedFalse();

        entries = entries.stream().sorted(Comparator.comparing(Request::getRequestDate)).toList();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println("Date,Site,Requested By,Type,Description,Amount,Status,Approval Stage");
            for (Request e : entries) {
                writer.println(
                        e.getRequestDate() + "," + escape(e.getSite().getSiteName()) + "," + escape(e.getRequestedBy()) + ","
                                + e.getRequestType() + "," + escape(e.getDescription()) + ","
                                + (e.getAmount() != null ? e.getAmount() : "") + "," + e.getStatus() + ","
                                + (e.getApprovalStage() != null ? e.getApprovalStage() : "")
                );
            }
        }
        return out.toByteArray();
    }


    @Override
    @Transactional(readOnly = true)
    public byte[] exportPdf(ReportType type, List<Long> siteIds) {
        return switch (type) {
            case LEDGER -> ledgerPdf(siteIds);
            case TRAVEL_EXPENSE -> travelExpensePdf(siteIds);
            case REQUEST -> requestPdf(siteIds);
        };
    }

    private byte[] ledgerPdf(List<Long> siteIds) {

        List<Ledger> entries = (siteIds != null && !siteIds.isEmpty())
                ? ledgerRepository.findAllBySite_IdInAndDeletedFalse(siteIds)
                : ledgerRepository.findAllByDeletedFalseAndSiteDeletedFalse();

        entries = entries.stream().sorted(Comparator.comparing(Ledger::getTransactionDate)).toList();

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

            addTitle(document, "Ledger Report", siteIds, titleFont);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 3, 2, 2, 2, 4});
            addHeaderCell(table, "Date", headerFont);
            addHeaderCell(table, "Site", headerFont);
            addHeaderCell(table, "Type", headerFont);
            addHeaderCell(table, "Source", headerFont);
            addHeaderCell(table, "Amount", headerFont);
            addHeaderCell(table, "Description", headerFont);

            BigDecimal totalCredit = BigDecimal.ZERO;
            BigDecimal totalDebit = BigDecimal.ZERO;

            for (Ledger e : entries) {
                addRow(table, cellFont, e.getTransactionDate().toString(), e.getSite().getSiteName(),
                        e.getEntryType().toString(), e.getSourceType().toString(), e.getAmount().toString(),
                        e.getDescription() == null ? "" : e.getDescription());
                if (e.getEntryType() == LedgerEntryType.CREDIT) totalCredit = totalCredit.add(e.getAmount());
                else totalDebit = totalDebit.add(e.getAmount());
            }
            document.add(table);

            Paragraph summary = new Paragraph(
                    "\nTotal Credit: Rs. " + totalCredit + "   |   Total Debit: Rs. " + totalDebit
                            + "   |   Net Balance: Rs. " + totalCredit.subtract(totalDebit), headerFont);
            summary.setSpacingBefore(15);
            document.add(summary);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate ledger PDF report", e);
        } finally {
            document.close();
        }
        return out.toByteArray();
    }

    private byte[] travelExpensePdf(List<Long> siteIds) {

        List<TravelExpense> entries = (siteIds != null && !siteIds.isEmpty())
                ? travelExpenseRepository.findAllBySite_IdInAndDeletedFalse(siteIds)
                : travelExpenseRepository.findAllByDeletedFalseAndSiteDeletedFalse();

        entries = entries.stream().sorted(Comparator.comparing(TravelExpense::getTravelDate)).toList();

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD);
            Font cellFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

            addTitle(document, "Travel Expense Report", siteIds, titleFont);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            addHeaderCell(table, "Date", headerFont);
            addHeaderCell(table, "Site", headerFont);
            addHeaderCell(table, "Employee", headerFont);
            addHeaderCell(table, "Route", headerFont);
            addHeaderCell(table, "Cost", headerFont);
            addHeaderCell(table, "Status", headerFont);
            addHeaderCell(table, "Bill", headerFont);

            BigDecimal totalCost = BigDecimal.ZERO;

            for (TravelExpense e : entries) {
                addRow(table, cellFont, e.getTravelDate().toString(), e.getSite().getSiteName(), e.getEmployeeName(),
                        e.getFromLocation() + " → " + e.getToLocation(), e.getTravelCost().toString(),
                        e.getTravelStatus().toString(), Boolean.TRUE.equals(e.getBillAttached()) ? "Yes" : "No");
                totalCost = totalCost.add(e.getTravelCost());
            }
            document.add(table);

            Paragraph summary = new Paragraph("\nTotal Travel Cost: Rs. " + totalCost, headerFont);
            summary.setSpacingBefore(15);
            document.add(summary);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate travel expense PDF report", e);
        } finally {
            document.close();
        }
        return out.toByteArray();
    }

    private byte[] requestPdf(List<Long> siteIds) {

        List<Request> entries = (siteIds != null && !siteIds.isEmpty())
                ? requestRepository.findAllBySite_IdInAndDeletedFalse(siteIds)
                : requestRepository.findAllByDeletedFalseAndSiteDeletedFalse();

        entries = entries.stream().sorted(Comparator.comparing(Request::getRequestDate)).toList();

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD);
            Font cellFont = new Font(Font.HELVETICA, 8, Font.NORMAL);

            addTitle(document, "Requests Report", siteIds, titleFont);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            addHeaderCell(table, "Date", headerFont);
            addHeaderCell(table, "Site", headerFont);
            addHeaderCell(table, "Requested By", headerFont);
            addHeaderCell(table, "Type", headerFont);
            addHeaderCell(table, "Description", headerFont);
            addHeaderCell(table, "Amount", headerFont);
            addHeaderCell(table, "Status", headerFont);

            for (Request e : entries) {
                addRow(table, cellFont, e.getRequestDate().toString(), e.getSite().getSiteName(), e.getRequestedBy(),
                        e.getRequestType().toString(), e.getDescription() == null ? "" : e.getDescription(),
                        e.getAmount() != null ? e.getAmount().toString() : "-", e.getStatus().toString());
            }
            document.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate requests PDF report", e);
        } finally {
            document.close();
        }
        return out.toByteArray();
    }


    private void addTitle(Document document, String base, List<Long> siteIds, Font titleFont) throws Exception {
        String suffix = (siteIds == null || siteIds.isEmpty())
                ? " - All Sites"
                : siteIds.size() == 1 ? " - 1 Site" : " - " + siteIds.size() + " Selected Sites";
        Paragraph heading = new Paragraph(base + suffix, titleFont);
        heading.setAlignment(Element.ALIGN_CENTER);
        heading.setSpacingAfter(15);
        document.add(heading);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(230, 230, 230));
        table.addCell(cell);
    }

    private void addRow(PdfPTable table, Font font, String... values) {
        for (String v : values) {
            table.addCell(new PdfPCell(new Phrase(v, font)));
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        String cleaned = value.replace("\"", "\"\"");
        return cleaned.contains(",") ? "\"" + cleaned + "\"" : cleaned;
    }
}