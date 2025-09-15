package com.aiassistant.aiassistant.service;

import com.aiassistant.aiassistant.exception.AppException;
import com.aiassistant.aiassistant.model.Message;
import com.aiassistant.aiassistant.repository.MessageRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MessageService {

    private final LlmService llmService;
    private final MessageRepository messageRepository;

    public MessageService(LlmService llmService, MessageRepository messageRepository) {
        this.llmService = llmService;
        this.messageRepository = messageRepository;
    }

    public Message createMessage(Message userMessage, JwtAuthenticationToken auth) throws AppException {
        String username = auth.getToken().getClaim("preferred_username");
        Long userId = auth.getToken().getClaim("user_id");

        userMessage.setCreatedAt(Instant.now());
        userMessage.setCreatedByUserId(userId);
        messageRepository.save(userMessage);

        String llmReply = llmService.generateReply(userMessage.getContent());

        Message assistantMessage = new Message();
        assistantMessage.setContent(llmReply);
        assistantMessage.setChatId(userMessage.getChatId());
        assistantMessage.setCreatedAt(Instant.now());
        assistantMessage.setCreatedByUserId(null); // system message

        messageRepository.save(assistantMessage);

        return assistantMessage;
    }
}