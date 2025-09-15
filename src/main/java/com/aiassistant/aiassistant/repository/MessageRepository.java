package com.aiassistant.aiassistant.repository;

import com.aiassistant.aiassistant.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}