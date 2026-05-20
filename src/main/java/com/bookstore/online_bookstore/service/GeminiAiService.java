package com.bookstore.online_bookstore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeminiAiService {

    // Pulls the API key securely from application.properties!
    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    // 🛠️ HELPER METHOD: Handles all the complex HTTP communication to Google
    private String callGeminiApi(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gemini requires a very specific JSON structure
        String safePrompt = prompt.replace("\"", "\\\"").replace("\n", " ");
        String requestJson = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + safePrompt + "\" }] }] }";

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL + apiKey, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            
            // Navigate Gemini's JSON response tree
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText().trim();
        } catch (Exception e) {
            System.err.println("AI Communication Error: " + e.getMessage());
            return "AI Service is currently unavailable.";
        }
    }

    // 1. ENHANCE SYNOPSIS
    public String enhanceSynopsis(String roughDraft) {
        String prompt = "You are an expert publishing editor. Rewrite the following book synopsis to be dramatic, professional, and captivating for a retail bookstore. Return ONLY the final synopsis. Original: " + roughDraft;
        return callGeminiApi(prompt);
    }

    // 2. SENTIMENT ANALYSIS
    public String analyzeSentiment(String reviewText) {
        String prompt = "Analyze the sentiment of this book review. Reply with EXACTLY ONE WORD: POSITIVE, NEUTRAL, or NEGATIVE. Review: " + reviewText;
        String response = callGeminiApi(prompt).toUpperCase().replaceAll("[^A-Z]", "");
        
        if (response.contains("POSITIVE")) return "POSITIVE";
        if (response.contains("NEGATIVE")) return "NEGATIVE";
        return "NEUTRAL";
    }

    // 3. STORE CHATBOT (Naive RAG)
    public String chatWithDatabase(String userQuery, String catalogData) {
        String prompt = "You are the Nexus Bookstore AI Assistant. Your ONLY knowledge is this inventory list: [" + catalogData + "]. " +
                "Customer asks: '" + userQuery + "'. " +
                "Rules: 1. If in stock, confirm and give price. 2. If not, apologize. 3. Keep it under 3 sentences.";
        return callGeminiApi(prompt);
    }

    // 4. APOLOGY GENERATOR
    public String generateApologyEmail(String customerName, String bookTitle, String reviewText) {
        String prompt = "Write a professional, empathetic apology email to customer " + customerName + 
                " who left this bad review for '" + bookTitle + "': '" + reviewText + "'. Offer a refund/replacement. Sign as 'Nexus Customer Support'.";
        return callGeminiApi(prompt);
    }

    // 5. REVIEW SUMMARIZER
    public String summarizeBookReviews(String reviewsText) {
        String prompt = "Write exactly ONE short sentence summarizing the overall sentiment of these reader reviews. Do NOT invent details. Reviews: " + reviewsText;
        return callGeminiApi(prompt);
    }
}