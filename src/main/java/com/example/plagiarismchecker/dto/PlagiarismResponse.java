package com.example.plagiarismchecker.dto;

import java.util.List;

public class PlagiarismResponse {

    private double similarity;
    private double semanticSimilarity;
    private double paraphraseRisk;
    private double aiGeneratedRisk;
    private double aiConfidenceScore;

    private String explanation;

    private List<String> matchedWords;
    private List<String> highlightedSentences;
    private List<String> matchedSources;

    private String text1;
    private String text2;

    public PlagiarismResponse() {
    }

    public PlagiarismResponse(double similarity, List<String> matchedWords, String text1, String text2) {
        this.similarity = similarity;
        this.matchedWords = matchedWords;
        this.text1 = text1;
        this.text2 = text2;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public double getSemanticSimilarity() {
        return semanticSimilarity;
    }

    public void setSemanticSimilarity(double semanticSimilarity) {
        this.semanticSimilarity = semanticSimilarity;
    }

    public double getParaphraseRisk() {
        return paraphraseRisk;
    }

    public void setParaphraseRisk(double paraphraseRisk) {
        this.paraphraseRisk = paraphraseRisk;
    }

    public double getAiGeneratedRisk() {
        return aiGeneratedRisk;
    }

    public void setAiGeneratedRisk(double aiGeneratedRisk) {
        this.aiGeneratedRisk = aiGeneratedRisk;
    }

    public double getAiConfidenceScore() {
        return aiConfidenceScore;
    }

    public void setAiConfidenceScore(double aiConfidenceScore) {
        this.aiConfidenceScore = aiConfidenceScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getMatchedWords() {
        return matchedWords;
    }

    public void setMatchedWords(List<String> matchedWords) {
        this.matchedWords = matchedWords;
    }

    public List<String> getHighlightedSentences() {
        return highlightedSentences;
    }

    public void setHighlightedSentences(List<String> highlightedSentences) {
        this.highlightedSentences = highlightedSentences;
    }

    public List<String> getMatchedSources() {
        return matchedSources;
    }

    public void setMatchedSources(List<String> matchedSources) {
        this.matchedSources = matchedSources;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }
}