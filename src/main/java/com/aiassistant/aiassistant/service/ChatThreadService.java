package com.aiassistant.aiassistant.service;

import com.aiassistant.aiassistant.model.ChatThread;
import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.repository.ChatThreadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatThreadService {

    private final ChatThreadRepository threadRepository;

    public ChatThreadService(ChatThreadRepository threadRepository) {
        this.threadRepository = threadRepository;
    }

    public ChatThread createThread(User user, String title) {
        ChatThread thread = new ChatThread();
        thread.setUser(user);
        thread.setTitle(title);
        return threadRepository.save(thread);
    }

    public List<ChatThread> getThreadsForUser(User user) {
        return threadRepository.findByUser(user);
    }

    public ChatThread findById(Long id) {
        return threadRepository.findById(id).orElse(null);
    }
}