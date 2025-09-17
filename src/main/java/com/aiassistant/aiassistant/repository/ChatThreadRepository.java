package com.aiassistant.aiassistant.repository;

import com.aiassistant.aiassistant.model.ChatThread;
import com.aiassistant.aiassistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
    List<ChatThread> findByUser(User user);
}