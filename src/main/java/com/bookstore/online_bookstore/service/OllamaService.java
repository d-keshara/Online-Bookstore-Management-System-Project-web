package com.bookstore.online_bookstore.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OllamaService {

    private final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private final String MODEL_NAME = "llama3";

    public String enhanceSynopsis(String roughDraft) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "You are an expert publishing editor. Rewrite the following book synopsis to be dramatic, professional, and captivating for a retail bookstore. Do not include any conversational text like 'Here is your rewrite', just return the final synopsis text itself. \n\nOriginal Synopsis: " + roughDraft;
        String requestJson = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                MODEL_NAME, prompt.replace("\"", "\\\"").replace("\n", " "));

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OLLAMA_API_URL, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("response").asText().trim();
        } catch (Exception e) {
            return "Error: Could not connect to local Ollama server.";
        }
    }

    public String analyzeSentiment(String reviewText) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "You are an expert customer service AI. Analyze the sentiment of the following book review. Reply with EXACTLY one word: POSITIVE, NEUTRAL, or NEGATIVE. Do not provide any other text, explanation, or punctuation. \n\nReview: " + reviewText;
        String requestJson = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                MODEL_NAME, prompt.replace("\"", "\\\"").replace("\n", " "));

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OLLAMA_API_URL, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String sentiment = root.path("response").asText().trim().toUpperCase().replace(".", "");

            if (sentiment.contains("POSITIVE")) return "POSITIVE";
            if (sentiment.contains("NEGATIVE")) return "NEGATIVE";
            return "NEUTRAL";
        } catch (Exception e) {
            return "NEUTRAL";
        }
    }

    public String chatWithDatabase(String userQuery, String catalogData) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "You are the Nexus Bookstore AI Assistant. Your ONLY knowledge is this exact inventory list: [" + catalogData + "]. \n\n" +
                "Customer asks: '" + userQuery + "'\n\n" +
                "Rules:\n" +
                "1. If they ask about a book IN the list, say 'Yes, we have [Title] in stock for [Price].'\n" +
                "2. If they ask for a book or author NOT in the list, apologize and say 'Sorry, we do not carry that.'\n" +
                "3. NEVER recommend a book that is not in the list.\n" +
                "4. Keep your answer polite and under 3 sentences.";

        String requestJson = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                MODEL_NAME, prompt.replace("\"", "\\\"").replace("\n", " "));

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OLLAMA_API_URL, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("response").asText().trim();
        } catch (Exception e) {
            return "I'm sorry, my systems are currently offline.";
        }
    }

    public String generateApologyEmail(String customerName, String bookTitle, String reviewText) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "You are an expert customer service manager for Nexus Bookstore. A customer named " + customerName + " left a highly negative review for the book '" + bookTitle + "'. Here is their review: '" + reviewText + "'. Write a professional, empathetic, and concise apology email to this customer. Offer to look into the issue or provide a refund/replacement. Do not include subject lines, just the email body. Sign it 'Nexus Customer Support'.";

        String requestJson = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                MODEL_NAME, prompt.replace("\"", "\\\"").replace("\n", " "));

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OLLAMA_API_URL, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("response").asText().trim();
        } catch (Exception e) {
            return "Error generating email. Please write manually.";
        }
    }

    public String summarizeBookReviews(String reviewsText) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "You are an expert book review summarizer. Read the following reader reviews. " +
                "STRICT RULES: " +
                "1. Write exactly ONE short sentence summarizing the overall sentiment. " +
                "2. Do NOT invent plots, characters, or details that are not in the reviews. " +
                "3. Do NOT use conversational filler (e.g., 'Here is the summary'). " +
                "4. If the reviews are extremely short or lack detail, simply reply exactly with: 'Readers generally left positive feedback based on initial reviews.' " +
                "\n\nReviews to summarize: " + reviewsText;

        String requestJson = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                MODEL_NAME, prompt.replace("\"", "\\\"").replace("\n", " "));

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OLLAMA_API_URL, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("response").asText().trim();
        } catch (Exception e) {
            return "Unable to generate summary at this time.";
        }
    }
}