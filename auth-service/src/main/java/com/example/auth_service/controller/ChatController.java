package com.example.auth_service.controller;

import com.example.auth_service.model.ChatMessage;
import com.example.auth_service.repository.ChatMessageRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "Контроллер для управления чатами")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(message);
        log.info("Sending message from {} to {}", message.getFromId(), message.getToId());
        messagingTemplate.convertAndSend("/user/" + message.getToId().toString() + "/queue/messages", message);
        messagingTemplate.convertAndSend("/user/" + message.getFromId().toString() + "/queue/messages", message);
    }

    // Получить все чаты консультанта (уникальные userId, которые писали консультанту)
    @GetMapping("/consultant/{consultantId}/chats")
    public List<UUID> getConsultantChats(@PathVariable UUID consultantId) {
        // Все уникальные fromId, которые писали этому консультанту
        return chatMessageRepository.findByToIdOrderByTimestampDesc(consultantId)
                .stream().map(ChatMessage::getFromId).distinct().toList();
    }

    // Получить историю сообщений между консультантом и пользователем
    @GetMapping("/chat/{chatId}")
    public List<ChatMessage> getChatHistory(@PathVariable String chatId) {
        return chatMessageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }

    // Получить все чаты пользователя (уникальные собеседники)
    @GetMapping("/user/{userId}/chats")
    public List<UUID> getUserChats(@PathVariable UUID userId) {
        return chatMessageRepository.findByFromIdOrToIdOrderByTimestampDesc(userId, userId)
            .stream()
            .flatMap(msg -> java.util.List.of(msg.getFromId(), msg.getToId()).stream())
            .filter(id -> !id.equals(userId))
            .distinct()
            .toList();
    }
} 