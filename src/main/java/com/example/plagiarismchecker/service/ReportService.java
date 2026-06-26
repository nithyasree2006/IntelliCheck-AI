package com.example.plagiarismchecker.service;

import com.example.plagiarismchecker.model.Report;
import com.example.plagiarismchecker.repository.ReportRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Report> getReportsByUserId(Long userId) {
        return reportRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    public boolean deleteReportById(Long id) {
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean deleteReportByIdAndUserId(Long reportId, Long userId) {
        Report report = reportRepository.findById(reportId).orElse(null);

        if (report == null) {
            return false;
        }

        if (userId == null) {
            reportRepository.delete(report);
            return true;
        }

        if (report.getUser() == null || report.getUser().getId() == null) {
            return false;
        }

        if (!report.getUser().getId().equals(userId)) {
            return false;
        }

        reportRepository.delete(report);
        return true;
    }

    public void deleteAllReports() {
        reportRepository.deleteAll();
    }

    public void deleteAllReportsByUserId(Long userId) {
        List<Report> reports = reportRepository.findByUserIdOrderByCreatedAtDesc(userId);
        reportRepository.deleteAll(reports);
    }

    public byte[] generatePdfReport(String originalText, String analyzedText) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margin = 50;
            float y = 780;
            float leading = 16;
            float width = page.getMediaBox().getWidth() - (2 * margin);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // TITLE
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Plagiarism Report");
            contentStream.endText();

            y -= 35;

            // ORIGINAL TEXT TITLE
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 13);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Original Text:");
            contentStream.endText();

            y -= 20;

            // ORIGINAL TEXT BODY
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 11);
            contentStream.newLineAtOffset(margin, y);

            List<String> originalLines = splitText(originalText, 95);
            for (String line : originalLines) {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -leading);
            }
            contentStream.endText();

            y -= (originalLines.size() * leading) + 20;

            // if original text was long, new page logic could be added later
            // for now this is still much safer than your current version

            // COMPARED TEXT TITLE
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 13);
            contentStream.newLineAtOffset(margin, y);
            contentStream.showText("Compared Text:");
            contentStream.endText();

            y -= 20;

            // COMPARED TEXT BODY
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 11);
            contentStream.newLineAtOffset(margin, y);

            List<String> analyzedLines = splitText(analyzedText, 95);
            for (String line : analyzedLines) {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -leading);
            }
            contentStream.endText();

            contentStream.close();
            document.save(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private List<String> splitText(String text, int maxLineLength) {
        if (text == null || text.isBlank()) {
            return List.of("N/A");
        }

        String[] words = text.replace("\n", " ").trim().split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxLineLength) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) {
                    line.append(" ");
                }
                line.append(word);
            }
        }

        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines;
    }
}