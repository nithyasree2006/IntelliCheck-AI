package com.example.plagiarismchecker.controller;

import com.example.plagiarismchecker.model.Report;
import com.example.plagiarismchecker.service.ReportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // GET ALL REPORTS FOR LOGGED-IN USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Report>> getReportsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getReportsByUserId(userId));
    }

    // GET SINGLE REPORT BY ID WITH USER CHECK
    @GetMapping("/{id}")
    public ResponseEntity<Report> getById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId
    ) {
        Report report = reportService.getReportById(id);

        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        if (userId != null) {
            if (report.getUser() == null || report.getUser().getId() == null) {
                return ResponseEntity.status(403).build();
            }

            if (!report.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.ok(report);
    }

    // DELETE ONE REPORT WITH USER CHECK
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReport(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId
    ) {
        boolean deleted = reportService.deleteReportByIdAndUserId(id, userId);

        if (deleted) {
            return ResponseEntity.ok("Report deleted successfully");
        }

        return ResponseEntity.status(404).body("Report not found or does not belong to this user");
    }

    // DELETE ALL REPORTS OF ONE USER
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteAllReportsOfUser(@PathVariable Long userId) {
        reportService.deleteAllReportsByUserId(userId);
        return ResponseEntity.ok("All reports deleted successfully");
    }

    // OPTIONAL FULL SYSTEM DELETE
    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllReports() {
        reportService.deleteAllReports();
        return ResponseEntity.ok("All reports deleted successfully");
    }

    // DOWNLOAD PDF FROM SAVED REPORT TEXTS
    @PostMapping("/download-report")
    public ResponseEntity<ByteArrayResource> downloadReport(@RequestBody Report request) {

        byte[] pdf = reportService.generatePdfReport(
                request.getOriginalText(),
                request.getAnalyzedText()
        );

        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plagiarism-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(resource);
    }
}