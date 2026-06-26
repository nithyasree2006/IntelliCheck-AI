package com.example.plagiarismchecker.service;

import com.example.plagiarismchecker.dto.PlagiarismResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfReportService {

    public byte[] generateReport(PlagiarismResponse response) throws Exception {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            float y = 750;

            // Title
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.newLineAtOffset(50, y);
            content.showText(cleanForPdf("IntelliCheck AI - Plagiarism Report"));
            content.endText();

            y -= 40;

            y = writeLine(content, "Similarity: " + format(response.getSimilarity()) + "%", y);
            y = writeLine(content, "Semantic Similarity: " + format(response.getSemanticSimilarity()) + "%", y);
            y = writeLine(content, "Paraphrase Risk: " + format(response.getParaphraseRisk()) + "%", y);
            y = writeLine(content, "AI Content Risk: " + format(response.getAiGeneratedRisk()) + "%", y);
            y = writeLine(content, "AI Confidence Score: " + format(response.getAiConfidenceScore()) + "%", y);

            y -= 15;
            y = writeLine(content, "Matched Words:", y);
            y = writeWrappedLine(content, safeJoin(response.getMatchedWords()), y);

            y -= 15;
            y = writeLine(content, "Highlighted Sentences:", y);

            List<String> highlighted = response.getHighlightedSentences();
            if (highlighted != null && !highlighted.isEmpty()) {
                for (String sentence : highlighted) {
                    y = writeWrappedLine(content, "- " + sentence, y);
                    if (y < 80) break;
                }
            } else {
                y = writeLine(content, "No strong sentence-level matches found.", y);
            }

            y -= 15;
            y = writeLine(content, "AI Explanation:", y);
            y = writeWrappedLine(content, response.getExplanation(), y);

            content.close();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private float writeLine(PDPageContentStream content, String text, float y) throws Exception {
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 12);
        content.newLineAtOffset(50, y);
        content.showText(cleanForPdf(text));
        content.endText();
        return y - 20;
    }

    private float writeWrappedLine(PDPageContentStream content, String text, float y) throws Exception {
        String safeText = cleanForPdf(text);
        int maxChars = 90;

        for (int i = 0; i < safeText.length(); i += maxChars) {
            String part = safeText.substring(i, Math.min(i + maxChars, safeText.length()));
            y = writeLine(content, part, y);
            if (y < 80) break;
        }
        return y;
    }

    private String safeJoin(List<String> list) {
        if (list == null || list.isEmpty()) return "None";
        return String.join(", ", list);
    }

    /**
     * Removes unsupported characters for PDFBox Helvetica font:
     * - emojis
     * - non-ASCII symbols
     * - line breaks replaced with spaces
     */
    private String cleanForPdf(String text) {
        if (text == null) return "";

        // replace new lines with spaces
        text = text.replaceAll("[\\r\\n]+", " ");

        // remove characters outside basic printable ASCII
        // keeps letters, numbers, punctuation
        text = text.replaceAll("[^\\x20-\\x7E]", "");

        return text;
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }
}