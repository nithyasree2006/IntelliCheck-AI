package com.example.plagiarismchecker.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class FileExtractionService {

    public String extractText(MultipartFile file) {
        try {
            String name = Optional.ofNullable(file.getOriginalFilename())
                    .orElse("")
                    .toLowerCase();

            if (name.endsWith(".txt")) {
                return new String(file.getBytes());
            }

            if (name.endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(file.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                }
            }

            if (name.endsWith(".docx")) {
                try (XWPFDocument doc = new XWPFDocument(file.getInputStream());
                     XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                    return extractor.getText();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
