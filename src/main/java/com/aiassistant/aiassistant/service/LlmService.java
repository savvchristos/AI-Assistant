package com.aiassistant.aiassistant.service;

import com.aiassistant.aiassistant.model.llm.LlmRequest;
import com.aiassistant.aiassistant.model.llm.LlmResponse;
import com.aiassistant.aiassistant.model.llm.LlmMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        System.out.println("🔑 API Key loaded: " + (apiKey != null ? "[REDACTED]" : "null"));
        System.out.println("🌐 API URL: " + apiUrl);
        System.out.println("🧠 Model: " + model);
    }

    public String generateReply(String userInput) {
        RestTemplate client = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        LlmMessage systemMessage = new LlmMessage("system", "You are a helpful assistant.");
        LlmMessage userMessage = new LlmMessage("user", userInput);

        LlmRequest request = new LlmRequest(model, List.of(systemMessage, userMessage));
        HttpEntity<LlmRequest> entity = new HttpEntity<>(request, headers);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(request);
            System.out.println("📨 JSON Payload: " + jsonPayload);

            ResponseEntity<LlmResponse> response = client.postForEntity(apiUrl, entity, LlmResponse.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                System.err.println("⚠️ Unexpected response: " + response.getStatusCode());
                return "Error: Unexpected response from LLM";
            }

            LlmResponse llmResponse = response.getBody();
            if (llmResponse.getChoices() == null || llmResponse.getChoices().isEmpty()) {
                System.err.println("⚠️ No choices returned in response");
                return "Error: No reply generated";
            }

            String reply = llmResponse.getChoices().get(0).getMessage().getContent();
            System.out.println("🤖 LLM Reply: " + reply);
            return reply;

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