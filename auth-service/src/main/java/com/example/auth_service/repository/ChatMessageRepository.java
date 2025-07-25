package com.example.auth_service.repository;

import com.example.auth_service.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByChatIdOrderByTimestampAsc(String chatId);
    List<ChatMessage> findByToIdOrderByTimestampDesc(UUID toId);
    List<ChatMessage> findByFromIdOrderByTimestampDesc(UUID fromId);
    List<ChatMessage> findByFromIdOrToIdOrderByTimestampDesc(UUID fromId, UUID toId);
} 