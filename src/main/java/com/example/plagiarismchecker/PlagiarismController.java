package com.example.plagiarismchecker;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlagiarismController {

    @PostMapping("/check")
    public Map<String, Object> checkPlagiarism(@RequestBody Map<String, String> request) {
        String text1 = request.get("text1");
        String text2 = request.get("text2");
        return calculateSimilarity(text1, text2);
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadFiles(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) {

        String text1 = extractText(file1);
        String text2 = extractText(file2);

        Map<String, Object> result = calculateSimilarity(text1, text2);

        // sending extracted text back to frontend for highlighting
        result.put("text1", text1);
        result.put("text2", text2);

        return result;
    }

    private Map<String, Object> calculateSimilarity(String t1, String t2) {

        if (t1 == null) t1 = "";
        if (t2 == null) t2 = "";

        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "i","me","my","myself","we","our","ours","ourselves","you","your","yours",
                "yourself","yourselves","he","him","his","himself","she","her","hers",
                "herself","it","its","itself","they","them","their","theirs","themselves",
                "what","which","who","whom","this","that","these","those","am","is","are",
                "was","were","be","been","being","have","has","had","having","do","does",
                "did","doing","a","an","the","and","but","if","or","because","as","until",
                "while","of","at","by","for","with","about","against","between","into",
                "through","during","before","after","above","below","to","from","up","down",
                "in","out","on","off","over","under","again","further","then","once","here",
                "there","when","where","why","how","all","both","each","few","more","most",
                "other","some","such","no","nor","not","only","own","same","so","than",
                "too","very","s","t","can","will","just","don","should","now","d","ll",
                "m","o","re","ve","y","ain","aren","couldn","didn","doesn","hadn","hasn",
                "haven","isn","ma","mightn","mustn","needn","shan","shouldn","wasn","weren",
                "won","wouldn"
        ));

        String[] words1 = t1.toLowerCase().split("\\W+");
        String[] words2 = t2.toLowerCase().split("\\W+");

        Map<String, Integer> freq1 = new HashMap<>();
        Map<String, Integer> freq2 = new HashMap<>();

        for (String word : words1) {
            if (!stopWords.contains(word) && !word.isEmpty()) {
                freq1.put(word, freq1.getOrDefault(word, 0) + 1);
            }
        }

        for (String word : words2) {
            if (!stopWords.contains(word) && !word.isEmpty()) {
                freq2.put(word, freq2.getOrDefault(word, 0) + 1);
            }
        }

        Set<String> allWords = new HashSet<>();
        allWords.addAll(freq1.keySet());
        allWords.addAll(freq2.keySet());

        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        List<String> matchedWords = new ArrayList<>();

        for (String word : allWords) {
            int v1 = freq1.getOrDefault(word, 0);
            int v2 = freq2.getOrDefault(word, 0);

            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;

            if (v1 > 0 && v2 > 0) {
                matchedWords.add(word);
            }
        }

        double similarity = 0;
        if (norm1 != 0 && norm2 != 0) {
            similarity = (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2))) * 100;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("similarity", similarity);
        response.put("matchedWords", matchedWords);

        return response;
    }

    private String extractText(MultipartFile file) {
        try {
            String name = Optional.ofNullable(file.getOriginalFilename())
                    .orElse("").toLowerCase();

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