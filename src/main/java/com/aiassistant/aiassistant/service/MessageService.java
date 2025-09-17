package com.aiassistant.aiassistant.service;

import com.aiassistant.aiassistant.exception.AppException;
import com.aiassistant.aiassistant.model.ChatThread;
import com.aiassistant.aiassistant.model.Message;
import com.aiassistant.aiassistant.model.User;
import com.aiassistant.aiassistant.repository.ChatThreadRepository;
import com.aiassistant.aiassistant.repository.MessageRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class MessageService {

    private final LlmService llmService;
    private final MessageRepository messageRepository;
    private final ChatThreadRepository threadRepository;
    private final UserService userService;

    public MessageService(
            LlmService llmService,
            MessageRepository messageRepository,
            ChatThreadRepository threadRepository,
            UserService userService
    ) {
        this.llmService = llmService;
        this.messageRepository = messageRepository;
        this.threadRepository = threadRepository;
        this.userService = userService;
    }

    public Message createMessage(Message userMessage, OAuth2AuthenticationToken auth) throws AppException {
        String username = auth.getPrincipal().getAttribute("preferred_username");
        if (username == null) {
            throw new AppException("Missing preferred_username in authentication token");
        }

        User user = userService.getOrCreateUser(username);

        // Resolve thread
        ChatThread thread = userMessage.getThread();
        if (thread == null || thread.getId() == null) {
            throw new AppException("Missing thread reference");
        }

        Optional<ChatThread> optionalThread = threadRepository.findById(thread.getId());
        if (optionalThread.isEmpty() || !optionalThread.get().getUser().getId().equals(user.getId())) {
            throw new AppException("Invalid thread access");
        }

        userMessage.setUser(user);
        userMessage.setThread(optionalThread.get());
        userMessage.setCreatedAt(Instant.now());
        messageRepository.save(userMessage);

        String llmReply = llmService.generateReply(userMessage.getContent());

        Message assistantMessage = new Message();
        assistantMessage.setContent(llmReply);
        assistantMessage.setThread(optionalThread.get());
        assistantMessage.setUser(null); // system message
        assistantMessage.setCreatedAt(Instant.now());

        messageRepository.save(assistantMessage);

        return assistantMessage;
    }
}