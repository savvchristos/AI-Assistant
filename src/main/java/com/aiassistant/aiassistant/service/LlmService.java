package com.aiassistant.aiassistant.service;

import com.aiassistant.aiassistant.model.llm.LlmRequest;
import com.aiassistant.aiassistant.model.llm.LlmResponse;
import com.aiassistant.aiassistant.model.llm.LlmMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LlmService {

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.api.url}")
    private String apiUrl;

    @Value("${llm.api.model}")
    private String model;

    public String generateReply(String userInput) {
        RestTemplate client = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        LlmMessage systemMessage = new LlmMessage("system", "You are a helpful assistant.");
        LlmMessage userMessage = new LlmMessage("user", userInput);

        LlmRequest request = new LlmRequest(model, List.of(systemMessage, userMessage));
        HttpEntity<LlmRequest> entity = new HttpEntity<>(request, headers);

        // 🔍 Debug logging
        System.out.println("🔧 Sending request to Groq...");
        System.out.println("🔑 API Key: " + (apiKey != null ? "[REDACTED]" : "null"));
        System.out.println("🌐 URL: " + apiUrl);
        System.out.println("🧠 Model: " + model);
        System.out.println("📦 Headers: " + headers);

        try {
            // ✅ Serialize payload to JSON for inspection
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(request);
            System.out.println("📨 JSON Payload: " + jsonPayload);

            ResponseEntity<LlmResponse> response = client.postForEntity(apiUrl, entity, LlmResponse.class);
            return response.getBody().getChoices().get(0).getMessage().getContent();
        } catch (HttpClientErrorException e) {
            System.err.println("❌ HTTP error: " + e.getStatusCode());
            System.err.println("🧾 Response body: " + e.getResponseBodyAsString());
            return "Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unexpected error: " + e.getMessage();
        }
    }
}