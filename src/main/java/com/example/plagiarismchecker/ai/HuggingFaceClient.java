package com.example.plagiarismchecker.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class HuggingFaceClient {

    @Value("${huggingface.api.token:}")
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Double> getEmbeddings(String text) {

        // If token is missing, return dummy embedding so app still runs
        if (token == null || token.isBlank()) {
            return getDummyEmbedding(text);
        }

        try {
            String apiUrl =
                    "https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.trim());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("inputs", text == null ? "" : text);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    Object.class
            );

            Object responseBody = response.getBody();

            if (responseBody == null) {
                return getDummyEmbedding(text);
            }

            return parseEmbedding(responseBody);

        } catch (Exception e) {
            System.out.println("HuggingFace API failed, using fallback embedding: " + e.getMessage());
            return getDummyEmbedding(text);
        }
    }

    private List<Double> parseEmbedding(Object responseBody) {
        List<Double> result = new ArrayList<>();

        if (responseBody instanceof List<?> outerList) {

            // Case 1: [0.1, 0.2, 0.3]
            if (!outerList.isEmpty() && outerList.get(0) instanceof Number) {
                for (Object obj : outerList) {
                    result.add(((Number) obj).doubleValue());
                }
                return result;
            }

            // Case 2: [[0.1, 0.2, 0.3]]
            if (!outerList.isEmpty() && outerList.get(0) instanceof List<?> innerList) {
                for (Object obj : innerList) {
                    if (obj instanceof Number) {
                        result.add(((Number) obj).doubleValue());
                    }
                }
                return result;
            }
        }

        return result.isEmpty() ? getDummyEmbedding("") : result;
    }

    private List<Double> getDummyEmbedding(String text) {
        String safe = text == null ? "" : text.toLowerCase();
        List<Double> vector = new ArrayList<>();

        int len = safe.length();
        int words = safe.isBlank() ? 0 : safe.split("\\s+").length;
        int vowels = 0;

        for (char c : safe.toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) vowels++;
        }

        vector.add((double) len);
        vector.add((double) words);
        vector.add((double) vowels);
        vector.add((double) (len % 7 + 1));
        vector.add((double) (words % 5 + 1));

        return vector;
    }
}