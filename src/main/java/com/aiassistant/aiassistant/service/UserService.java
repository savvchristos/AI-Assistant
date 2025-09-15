package com.aiassistant.aiassistant.service;

import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreateUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            userRepository.save(user);
        }
        return user;
    }
}