package com.aiassistant.aiassistant.controller;

import com.aiassistant.aiassistant.model.ChatThread;
import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.service.ChatThreadService;
import com.aiassistant.aiassistant.service.UserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    private final ChatThreadService threadService;
    private final UserService userService;

    public ThreadController(ChatThreadService threadService, UserService userService) {
        this.threadService = threadService;
        this.userService = userService;
    }

    @PostMapping
    public ChatThread createThread(@RequestBody ChatThread thread, OAuth2AuthenticationToken auth) {
        String username = auth.getPrincipal().getAttribute("preferred_username");
        User user = userService.getOrCreateUser(username);
        return threadService.createThread(user, thread.getTitle());
    }

    @GetMapping
    public List<ChatThread> getUserThreads(OAuth2AuthenticationToken auth) {
        String username = auth.getPrincipal().getAttribute("preferred_username");
        User user = userService.getOrCreateUser(username);
        return threadService.getThreadsForUser(user);
    }
}