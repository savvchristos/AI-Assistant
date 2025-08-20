package com.aiassistant.aiassistant.controller;

import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.model.UserRepository;
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

    /**
     * Map root ("/") directly to the chat page.
     * This avoids the 404 when visiting http://localhost:8080/
     */
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        // Pass current username or null to the template
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
    public String register(@ModelAttribute User user, BindingResult res) {
        if (res.hasErrors()) {
            return "register";
        }
        // Encode password before saving
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/chat")
    public String chat(Authentication authentication, Model model) {
        // Also available on direct /chat route
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        return "chat";
    }
}