package com.aiassistant.aiassistant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Instant createdAt = Instant.now();

    @ManyToOne
    @JoinColumn(name = "thread_id")
    @JsonIgnore // ✅ Prevent recursion from Message → Thread → Messages → Thread
    private ChatThread thread;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore // ✅ Prevent recursion from Message → User → Messages → User
    private User user;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public ChatThread getThread() { return thread; }
    public void setThread(ChatThread thread) { this.thread = thread; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}