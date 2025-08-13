package com.aiassistant.aiassistant.controller;

import com.aiassistant.aiassistant.service.LLMService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final LLMService llmService;

    public ChatController(LLMService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> payload) {
        String prompt = payload.get("prompt");
        return llmService.generateReply(prompt);
    }
}