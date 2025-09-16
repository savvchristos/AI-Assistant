package com.aiassistant.aiassistant.controller;

import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    private final UserService userService;
    private final PasswordEncoder encoder;

    public WebController(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);

            // Ensure user exists in DB
            userService.getOrCreateUser(username);
        }
        return "chat";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, BindingResult res, Model model) {
        if (res.hasErrors()) {
            model.addAttribute("error", "Invalid input. Please try again.");
            return "register";
        }

        if (userService.getUserByUsername(user.getUsername()) != null) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Username already exists. Please choose another.");
            return "register";
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/chat")
    public String chat(Authentication authentication, Model model) {
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);

            // Ensure user exists in DB
            userService.getOrCreateUser(username);
        }
        return "chat";
    }
}