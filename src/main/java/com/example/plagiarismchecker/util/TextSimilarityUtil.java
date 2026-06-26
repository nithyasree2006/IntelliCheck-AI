package com.example.plagiarismchecker.util;

import java.util.*;

public class TextSimilarityUtil {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
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

    public static double calculateSimilarity(String t1, String t2) {
        if (t1 == null) t1 = "";
        if (t2 == null) t2 = "";

        String[] words1 = t1.toLowerCase().split("\\W+");
        String[] words2 = t2.toLowerCase().split("\\W+");

        Map<String, Integer> freq1 = new HashMap<>();
        Map<String, Integer> freq2 = new HashMap<>();

        for (String word : words1) {
            if (!STOP_WORDS.contains(word) && !word.isEmpty()) {
                freq1.put(word, freq1.getOrDefault(word, 0) + 1);
            }
        }

        for (String word : words2) {
            if (!STOP_WORDS.contains(word) && !word.isEmpty()) {
                freq2.put(word, freq2.getOrDefault(word, 0) + 1);
            }
        }

        Set<String> allWords = new HashSet<>();
        allWords.addAll(freq1.keySet());
        allWords.addAll(freq2.keySet());

        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (String word : allWords) {
            int v1 = freq1.getOrDefault(word, 0);
            int v2 = freq2.getOrDefault(word, 0);

            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        if (norm1 == 0 || norm2 == 0) return 0.0;

        return (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2))) * 100.0;
    }

    public static List<String> getMatchedWords(String t1, String t2) {
        if (t1 == null) t1 = "";
        if (t2 == null) t2 = "";

        String[] words1 = t1.toLowerCase().split("\\W+");
        String[] words2 = t2.toLowerCase().split("\\W+");

        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        for (String w : words1) {
            if (!STOP_WORDS.contains(w) && !w.isEmpty()) {
                set1.add(w);
            }
        }

        for (String w : words2) {
            if (!STOP_WORDS.contains(w) && !w.isEmpty()) {
                set2.add(w);
            }
        }

        List<String> matched = new ArrayList<>();

        for (String w : set1) {
            if (set2.contains(w)) {
                matched.add(w);
            }
        }

        Collections.sort(matched);
        return matched;
    }

    public static List<String> getHighlightedSentences(String originalText, String suspiciousText) {
        List<String> highlighted = new ArrayList<>();

        if (originalText == null) originalText = "";
        if (suspiciousText == null) suspiciousText = "";

        String[] originalSentences = splitSentences(originalText);
        String[] suspiciousSentences = splitSentences(suspiciousText);

        for (String suspectSentence : suspiciousSentences) {
            String cleanedSuspect = suspectSentence.trim();

            if (cleanedSuspect.isEmpty()) continue;

            double bestScore = 0.0;

            for (String originalSentence : originalSentences) {
                String cleanedOriginal = originalSentence.trim();
                if (cleanedOriginal.isEmpty()) continue;

                double score = calculateSimilarity(cleanedOriginal, cleanedSuspect);
                if (score > bestScore) {
                    bestScore = score;
                }
            }

            if (bestScore >= 45.0) {
                highlighted.add(cleanedSuspect + "  🔥 Match Score: " + String.format("%.2f", bestScore) + "%");
            }
        }

        return highlighted;
    }

    private static String[] splitSentences(String text) {
        return text.split("(?<=[.!?])\\s+");
    }
}