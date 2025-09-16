package com.aiassistant.aiassistant.controller;

import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        String username = principal.getName();
        return userService.getOrCreateUser(username);
    }

    @PostMapping("/update-display-name")
    public ResponseEntity<?> updateDisplayName(@RequestBody Map<String, String> payload,
                                               @AuthenticationPrincipal OAuth2User principal) {
        String username = principal.getName();
        String newDisplayName = payload.get("displayName");

        if (newDisplayName == null || newDisplayName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Display name cannot be empty");
        }

        boolean success = userService.updateDisplayName(username, newDisplayName);
        return success ? ResponseEntity.ok("Display name updated") : ResponseEntity.badRequest().body("Update failed");
    }
}