package com.aiassistant.aiassistant.controller;

import com.aiassistant.aiassistant.service.LlmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final LlmService llmService;

    public ChatController(LlmService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam("prompt") String prompt,
                                       @AuthenticationPrincipal OAuth2User principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            String username = principal.getAttribute("preferred_username");
            if (username == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing username attribute");
            }

            System.out.println("User: " + username + " sent prompt: " + prompt);
            String reply = llmService.generateReply(prompt);
            return ResponseEntity.ok(reply);

        } catch (Exception e) {
            e.printStackTrace(); // logs full stack trace
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }
}