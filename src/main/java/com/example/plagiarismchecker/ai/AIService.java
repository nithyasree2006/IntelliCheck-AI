package com.example.plagiarismchecker.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIService {

    @Autowired
    private HuggingFaceClient hfClient;

    // =========================
    // SEMANTIC SIMILARITY
    // Returns value in 0 to 100
    // =========================
    public double getSemanticSimilarity(String text1, String text2) {
        try {
            List<Double> emb1 = hfClient.getEmbeddings(text1);
            List<Double> emb2 = hfClient.getEmbeddings(text2);

            if (!isValidEmbedding(emb1) || !isValidEmbedding(emb2)) {
                return 0.0;
            }

            return cosineSimilarityPercent(emb1, emb2);

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // =========================
    // PARAPHRASE RISK
    // Returns value in 0 to 100
    // =========================
    public double detectParaphraseRisk(String text1, String text2) {
        try {
            List<Double> emb1 = hfClient.getEmbeddings(text1);
            List<Double> emb2 = hfClient.getEmbeddings(text2);

            if (!isValidEmbedding(emb1) || !isValidEmbedding(emb2)) {
                return 0.0;
            }

            return cosineSimilarityPercent(emb1, emb2);

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // =========================
    // AI GENERATED CONTENT RISK
    // Simple heuristic score in 0 to 100
    // =========================
    public double detectAIGeneratedContent(String text) {
        try {
            List<Double> emb = hfClient.getEmbeddings(text);

            if (!isValidEmbedding(emb)) {
                return 0.0;
            }

            double magnitude = 0.0;
            for (Double val : emb) {
                magnitude += val * val;
            }

            double norm = Math.sqrt(magnitude);

            // keep result between 0 and 100
            double score = (norm / emb.size()) * 1000.0;
            return clamp(score);

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // =========================
    // AI EXPLANATION
    // =========================
    public String generateAIExplanation(String text) {
        return "AI analyzed semantic embeddings using a transformer model. "
                + "Similarity patterns indicate possible copied, paraphrased, or AI-assisted content.";
    }

    // =========================
    // HELPER: VALIDATE EMBEDDING
    // =========================
    private boolean isValidEmbedding(List<Double> emb) {
        return emb != null && !emb.isEmpty();
    }

    // =========================
    // HELPER: COSINE SIMILARITY -> 0 to 100
    // =========================
    private double cosineSimilarityPercent(List<Double> v1, List<Double> v2) {
        if (v1 == null || v2 == null || v1.isEmpty() || v2.isEmpty() || v1.size() != v2.size()) {
            return 0.0;
        }

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            normA += v1.get(i) * v1.get(i);
            normB += v2.get(i) * v2.get(i);
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        double cosine = dot / (Math.sqrt(normA) * Math.sqrt(normB));

        // cosine can be -1 to 1
        // for plagiarism score we only want 0 to 100
        double percentage = cosine * 100.0;
        return clamp(percentage);
    }

    // =========================
    // HELPER: clamp any value to 0..100
    // =========================
    private double clamp(double value) {
        if (value < 0) return 0.0;
        if (value > 100) return 100.0;
        return value;
    }
}