package com.aiassistant.aiassistant.controller;

import com.aiassistant.aiassistant.model.ChatThread;
import com.aiassistant.aiassistant.model.Message;
import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.service.ChatThreadService;
import com.aiassistant.aiassistant.service.MessageService;
import com.aiassistant.aiassistant.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final MessageService messageService;
    private final UserService userService;
    private final ChatThreadService chatThreadService;

    public ChatController(MessageService messageService,
                          UserService userService,
                          ChatThreadService chatThreadService) {
        this.messageService = messageService;
        this.userService = userService;
        this.chatThreadService = chatThreadService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request,
                                       OAuth2AuthenticationToken auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            String username = auth.getPrincipal().getAttribute("preferred_username");
            if (username == null || username.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing username attribute");
            }

            if (request.getContent() == null || request.getContent().isBlank() || request.getThreadId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing content or threadId");
            }

            User user = userService.getOrCreateUser(username);
            ChatThread thread = chatThreadService.findById(request.getThreadId());

            if (thread == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thread not found");
            }

            Message userMessage = new Message();
            userMessage.setContent(request.getContent());
            userMessage.setThread(thread);
            userMessage.setUser(user);

            Message reply = messageService.createMessage(userMessage, auth);
            return ResponseEntity.ok(reply.getContent());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(@RequestParam Long threadId,
                                                     OAuth2AuthenticationToken auth) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String username = auth.getPrincipal().getAttribute("preferred_username");
            if (username == null || username.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            User user = userService.getOrCreateUser(username);
            ChatThread thread = chatThreadService.findById(threadId);

            if (thread == null || !thread.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Message> messages = thread.getMessages();
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static class ChatRequest {
        private String content;
        private Long threadId;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Long getThreadId() { return threadId; }
        public void setThreadId(Long threadId) { this.threadId = threadId; }
    }
}