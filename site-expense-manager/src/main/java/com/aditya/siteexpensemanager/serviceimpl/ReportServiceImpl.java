package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.entity.Ledger;
import com.aditya.siteexpensemanager.repository.LedgerRepository;
import com.aditya.siteexpensemanager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import com.aditya.siteexpensemanager.enums.LedgerEntryType;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;



import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final LedgerRepository ledgerRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportLedgerCsv(Long siteId) {

        List<Ledger> entries = (siteId != null)
                ? ledgerRepository.findAllBySiteIdAndDeletedFalse(siteId)
                : ledgerRepository.findAllByDeletedFalseAndSiteDeletedFalse();

        entries = entries.stream()
                .sorted(Comparator.comparing(Ledger::getTransactionDate))
                .toList();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {

            writer.println("Date,Site,Type,Source,Amount,Description");

            for (Ledger entry : entries) {
                writer.println(
                        entry.getTransactionDate() + ","
                                + escape(entry.getSite().getSiteName()) + ","
                                + entry.getEntryType() + ","
                                + entry.getSourceType() + ","
                                + entry.getAmount() + ","
                                + escape(entry.getDescription())
                );
            }
        }

        return out.toByteArray();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportLedgerPdf(Long siteId) {

        List<Ledger> entries = (siteId != null)
                ? ledgerRepository.findAllBySiteIdAndDeletedFalse(siteId)
                : ledgerRepository.findAllByDeletedFalseAndSiteDeletedFalse();

        entries = entries.stream()
                .sorted(Comparator.comparing(Ledger::getTransactionDate))
                .toList();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

            String title = (siteId != null)
                    ? "Ledger Report - Site #" + siteId
                    : "Ledger Report - All Sites";

            Paragraph heading = new Paragraph(title, titleFont);
            heading.setAlignment(Element.ALIGN_CENTER);
            heading.setSpacingAfter(15);
            document.add(heading);

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

            for (Ledger entry : entries) {

                table.addCell(new PdfPCell(new Phrase(entry.getTransactionDate().toString(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getSite().getSiteName(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getEntryType().toString(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getSourceType().toString(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getAmount().toString(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(
                        entry.getDescription() == null ? "" : entry.getDescription(), cellFont)));

                if (entry.getEntryType() == LedgerEntryType.CREDIT) {
                    totalCredit = totalCredit.add(entry.getAmount());
                } else {
                    totalDebit = totalDebit.add(entry.getAmount());
                }
            }

            document.add(table);

            Paragraph summary = new Paragraph(
                    "\nTotal Credit: Rs. " + totalCredit
                            + "   |   Total Debit: Rs. " + totalDebit
                            + "   |   Net Balance: Rs. " + totalCredit.subtract(totalDebit),
                    headerFont
            );
            summary.setSpacingBefore(15);
            document.add(summary);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate ledger PDF report", e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }

    private void addHeaderCell(PdfPTable table, String text, com.lowagie.text.Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(230, 230, 230));
        table.addCell(cell);
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        String cleaned = value.replace("\"", "\"\"");

        return cleaned.contains(",") ? "\"" + cleaned + "\"" : cleaned;
    }
}
