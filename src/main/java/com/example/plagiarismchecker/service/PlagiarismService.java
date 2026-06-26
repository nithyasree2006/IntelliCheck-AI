package com.example.plagiarismchecker.service;

import com.example.plagiarismchecker.ai.AIService;
import com.example.plagiarismchecker.dto.PlagiarismResponse;
import com.example.plagiarismchecker.util.TextSimilarityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PlagiarismService {

    @Autowired
    private AIService aiService;

    public PlagiarismResponse checkTextPlagiarism(String text1, String text2) {

        if (text1 == null) text1 = "";
        if (text2 == null) text2 = "";

        double similarity = TextSimilarityUtil.calculateSimilarity(text1, text2);
        List<String> matchedWords = TextSimilarityUtil.getMatchedWords(text1, text2);
        List<String> highlightedSentences = TextSimilarityUtil.getHighlightedSentences(text1, text2);

        double semanticSimilarity = clamp(aiService.getSemanticSimilarity(text1, text2));
        double paraphraseRisk = clamp(aiService.detectParaphraseRisk(text1, text2));
        double aiRisk = clamp(aiService.detectAIGeneratedContent(text2));

        String explanation;
        try {
            explanation = aiService.generateAIExplanation(text1 + " " + text2);
        } catch (Exception e) {
            explanation = "AI explanation could not be generated.";
        }

        double aiConfidenceScore = (
                (similarity * 0.35) +
                        (semanticSimilarity * 0.30) +
                        (paraphraseRisk * 0.20) +
                        (aiRisk * 0.15)
        );

        aiConfidenceScore = clamp(aiConfidenceScore);

        PlagiarismResponse response = new PlagiarismResponse();
        response.setSimilarity(clamp(similarity));
        response.setMatchedWords(matchedWords != null ? matchedWords : Collections.emptyList());
        response.setHighlightedSentences(highlightedSentences != null ? highlightedSentences : Collections.emptyList());
        response.setMatchedSources(Collections.emptyList());
        response.setText1(text1);
        response.setText2(text2);
        response.setSemanticSimilarity(semanticSimilarity);
        response.setParaphraseRisk(paraphraseRisk);
        response.setAiGeneratedRisk(aiRisk);
        response.setAiConfidenceScore(aiConfidenceScore);
        response.setExplanation(explanation);

        return response;
    }

    public PlagiarismResponse checkFilePlagiarism(String text1, String text2) {
        return checkTextPlagiarism(text1, text2);
    }

    public PlagiarismResponse analyze(String text1, String text2) {
        return checkTextPlagiarism(text1, text2);
    }

    private double clamp(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(100.0, value));
    }
}