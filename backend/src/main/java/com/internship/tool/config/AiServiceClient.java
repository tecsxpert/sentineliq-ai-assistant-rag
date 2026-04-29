// AiServiceClient.java
// Author: Kushal V R (AI Developer 3)
// Day 6 — Tool-75 AI Assistant with RAG
// This class is the bridge between Java backend and Flask AI service.
// It makes HTTP calls to all Flask endpoints using RestTemplate.
// If anything goes wrong, it returns null gracefully instead of crashing.

package com.internship.tool.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

@Component
public class AiServiceClient {

    // Logger so we can see what is happening in the console
    private static final Logger logger = LoggerFactory.getLogger(AiServiceClient.class);

    // RestTemplate is the tool we use to make HTTP calls to Flask
    private final RestTemplate restTemplate;

    // This reads the AI service URL from application.yml
    // So we never hardcode the URL — it comes from config
    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    // Constructor — we set up RestTemplate with 10 second timeout here
    // 10s connect timeout means if Flask doesn't respond in 10s, we give up
    // 10s read timeout means if Flask starts but takes too long, we give up
    public AiServiceClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }

    // --- Helper method to build JSON headers ---
    // Every request to Flask needs Content-Type: application/json
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // --- Call /describe endpoint ---
    // Sends input text to Flask and gets back a description
    // Returns null if anything goes wrong — never crashes the Java app
    public String describe(String input) {
        try {
            String url = aiServiceUrl + "/describe";

            Map<String, String> body = new HashMap<>();
            body.put("input", input);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, buildHeaders());

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("AiServiceClient: /describe call successful");
                return response.getBody();
            } else {
                logger.warn("AiServiceClient: /describe returned status {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            // If Flask is down or slow, we log the error and return null
            // We never throw the exception — Java backend keeps working normally
            logger.error("AiServiceClient: /describe call failed — {}", e.getMessage());
            return null;
        }
    }

    // --- Call /recommend endpoint ---
    // Sends input text to Flask and gets back 3 recommendations
    // Returns null gracefully on any error
    public String recommend(String input) {
        try {
            String url = aiServiceUrl + "/recommend";

            Map<String, String> body = new HashMap<>();
            body.put("input", input);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, buildHeaders());

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("AiServiceClient: /recommend call successful");
                return response.getBody();
            } else {
                logger.warn("AiServiceClient: /recommend returned status {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("AiServiceClient: /recommend call failed — {}", e.getMessage());
            return null;
        }
    }

    // --- Call /categorise endpoint ---
    // Sends input text to Flask and gets back a category and confidence score
    // Returns null gracefully on any error
    public String categorise(String input) {
        try {
            String url = aiServiceUrl + "/categorise";

            Map<String, String> body = new HashMap<>();
            body.put("input", input);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, buildHeaders());

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("AiServiceClient: /categorise call successful");
                return response.getBody();
            } else {
                logger.warn("AiServiceClient: /categorise returned status {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("AiServiceClient: /categorise call failed — {}", e.getMessage());
            return null;
        }
    }

    // --- Call /generate-report endpoint ---
    // Sends input to Flask and gets back a full AI generated report
    // This endpoint has stricter rate limit (10/min) so we handle 429 too
    // Returns null gracefully on any error
    public String generateReport(String input) {
        try {
            String url = aiServiceUrl + "/generate-report";

            Map<String, String> body = new HashMap<>();
            body.put("input", input);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, buildHeaders());

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("AiServiceClient: /generate-report call successful");
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                // 429 means rate limit exceeded — log it and return null
                logger.warn("AiServiceClient: /generate-report rate limit exceeded (429)");
                return null;
            } else {
                logger.warn("AiServiceClient: /generate-report returned status {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("AiServiceClient: /generate-report call failed — {}", e.getMessage());
            return null;
        }
    }

    // --- Call /query endpoint ---
    // This is the RAG query endpoint — sends a question and gets answer + sources
    // Returns null gracefully on any error
    public String query(String question) {
        try {
            String url = aiServiceUrl + "/query";

            Map<String, String> body = new HashMap<>();
            body.put("query", question);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, buildHeaders());

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("AiServiceClient: /query call successful");
                return response.getBody();
            } else {
                logger.warn("AiServiceClient: /query returned status {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("AiServiceClient: /query call failed — {}", e.getMessage());
            return null;
        }
    }

    // --- Call /health endpoint ---
    // Used to check if Flask AI service is up and running
    // Returns true if healthy, false if not reachable
    public boolean isHealthy() {
        try {
            String url = aiServiceUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("AiServiceClient: health check failed — {}", e.getMessage());
            return false;
        }
    }
}