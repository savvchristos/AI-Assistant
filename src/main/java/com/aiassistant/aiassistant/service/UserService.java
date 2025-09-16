package com.aiassistant.aiassistant.service;

import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreateUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPassword(null);
                    newUser.setDisplayName(username);
                    return userRepository.save(newUser);
                });
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean updateDisplayName(String username, String newDisplayName) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) return false;

        User user = optionalUser.get();
        user.setDisplayName(newDisplayName);
        userRepository.save(user);
        return true;
    }
}