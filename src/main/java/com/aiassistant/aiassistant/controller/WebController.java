package com.aiassistant.aiassistant.controller;

import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public WebController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
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

        if (userRepo.findByUsername(user.getUsername()) != null) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Username already exists. Please choose another.");
            return "register";
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/chat")
    public String chat(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        return "chat";
    }
}