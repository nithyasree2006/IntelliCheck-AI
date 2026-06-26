package com.example.plagiarismchecker.controller;

import com.example.plagiarismchecker.dto.PlagiarismRequest;
import com.example.plagiarismchecker.dto.PlagiarismResponse;
import com.example.plagiarismchecker.model.Report;
import com.example.plagiarismchecker.model.User;
import com.example.plagiarismchecker.repository.UserRepository;
import com.example.plagiarismchecker.service.FileExtractionService;
import com.example.plagiarismchecker.service.PdfReportService;
import com.example.plagiarismchecker.service.PlagiarismService;
import com.example.plagiarismchecker.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlagiarismController {

    private final PlagiarismService plagiarismService;
    private final FileExtractionService fileExtractionService;
    private final PdfReportService pdfReportService;
    private final ReportService reportService;
    private final UserRepository userRepository;

    public PlagiarismController(PlagiarismService plagiarismService,
                                FileExtractionService fileExtractionService,
                                PdfReportService pdfReportService,
                                ReportService reportService,
                                UserRepository userRepository) {
        this.plagiarismService = plagiarismService;
        this.fileExtractionService = fileExtractionService;
        this.pdfReportService = pdfReportService;
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @PostMapping("/check")
    public PlagiarismResponse checkPlagiarism(@RequestBody PlagiarismRequest request) {
        System.out.println("==== /check HIT ====");
        System.out.println("text1 = " + request.getText1());
        System.out.println("text2 = " + request.getText2());
        System.out.println("userId = " + request.getUserId());

        PlagiarismResponse response =
                plagiarismService.checkTextPlagiarism(request.getText1(), request.getText2());

        if (request.getUserId() != null) {
            Optional<User> userOptional = userRepository.findById(request.getUserId());

            System.out.println("User found? " + userOptional.isPresent());

            if (userOptional.isPresent()) {
                Report report = new Report();
                report.setOriginalText(request.getText1());
                report.setAnalyzedText(request.getText2());
                report.setSimilarity(response.getSimilarity());
                report.setSemanticSimilarity(response.getSemanticSimilarity());
                report.setParaphraseRisk(response.getParaphraseRisk());
                report.setAiGeneratedRisk(response.getAiGeneratedRisk());
                report.setAiConfidenceScore(response.getAiConfidenceScore());
                report.setExplanation(response.getExplanation());
                report.setMatchedWords(response.getMatchedWords());
                report.setHighlightedSentences(response.getHighlightedSentences());
                report.setCreatedAt(LocalDateTime.now());
                report.setUser(userOptional.get());

                Report saved = reportService.saveReport(report);
                System.out.println("REPORT SAVED WITH ID = " + saved.getId());
            } else {
                System.out.println("USER NOT FOUND. REPORT NOT SAVED.");
            }
        } else {
            System.out.println("NO USER ID RECEIVED. REPORT NOT SAVED.");
        }

        return response;
    }

    @PostMapping("/upload")
    public PlagiarismResponse uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("text1") String text1,
                                         @RequestParam(value = "userId", required = false) Long userId) throws Exception {

        System.out.println("==== /upload HIT ====");
        System.out.println("text1 = " + text1);
        System.out.println("userId = " + userId);

        String text2 = fileExtractionService.extractText(file);
        PlagiarismResponse response = plagiarismService.analyze(text1, text2);

        if (userId != null) {
            Optional<User> userOptional = userRepository.findById(userId);
            System.out.println("User found? " + userOptional.isPresent());

            if (userOptional.isPresent()) {
                Report report = new Report();
                report.setOriginalText(text1);
                report.setAnalyzedText(text2);
                report.setSimilarity(response.getSimilarity());
                report.setSemanticSimilarity(response.getSemanticSimilarity());
                report.setParaphraseRisk(response.getParaphraseRisk());
                report.setAiGeneratedRisk(response.getAiGeneratedRisk());
                report.setAiConfidenceScore(response.getAiConfidenceScore());
                report.setExplanation(response.getExplanation());
                report.setMatchedWords(response.getMatchedWords());
                report.setHighlightedSentences(response.getHighlightedSentences());
                report.setCreatedAt(LocalDateTime.now());
                report.setUser(userOptional.get());

                Report saved = reportService.saveReport(report);
                System.out.println("REPORT SAVED WITH ID = " + saved.getId());
            } else {
                System.out.println("USER NOT FOUND. REPORT NOT SAVED.");
            }
        } else {
            System.out.println("NO USER ID RECEIVED. REPORT NOT SAVED.");
        }

        return response;
    }

    @PostMapping("/download-report")
    public ResponseEntity<byte[]> downloadReport(@RequestBody PlagiarismRequest request) throws Exception {
        PlagiarismResponse response =
                plagiarismService.checkTextPlagiarism(request.getText1(), request.getText2());

        byte[] pdfBytes = pdfReportService.generateReport(response);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plagiarism-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}