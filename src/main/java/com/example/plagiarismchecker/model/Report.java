package com.example.plagiarismchecker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "LONGTEXT")
    private String originalText;

    @Column(columnDefinition = "LONGTEXT")
    private String analyzedText;

    private double similarity;
    private double semanticSimilarity;
    private double paraphraseRisk;
    private double aiGeneratedRisk;
    private double aiConfidenceScore;

    @Column(columnDefinition = "LONGTEXT")
    private String explanation;

    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "report_matched_words", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "word")
    private List<String> matchedWords;

    @ElementCollection
    @CollectionTable(name = "report_highlighted_sentences", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "sentence", length = 3000)
    private List<String> highlightedSentences;

    @ElementCollection
    @CollectionTable(name = "report_matched_sources", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "source", length = 1000)
    private List<String> matchedSources;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User user;

    public Report() {
    }

    public Long getId() {
        return id;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getAnalyzedText() {
        return analyzedText;
    }

    public void setAnalyzedText(String analyzedText) {
        this.analyzedText = analyzedText;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}