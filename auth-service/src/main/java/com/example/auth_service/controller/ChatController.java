package com.example.auth_service.controller;

import com.example.auth_service.model.ChatMessage;
import com.example.auth_service.repository.ChatMessageRepository;
import com.example.auth_service.dto.response.ChatMessageResponse;
import com.example.auth_service.dto.response.ChatListResponse;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "Контроллер для управления чатами")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) {
        log.info("Received chat message from {} to {}: {}", message.getFromId(), message.getToId(), message.getContent());
        
        try {
            message.setTimestamp(LocalDateTime.now());
            ChatMessage savedMessage = chatMessageRepository.save(message);
            log.debug("Message saved with id: {}", savedMessage.getId());
            
            String toUserDestination = "/user/" + message.getToId().toString() + "/queue/messages";
            String fromUserDestination = "/user/" + message.getFromId().toString() + "/queue/messages";
            
            log.debug("Sending message to destinations: {} and {}", toUserDestination, fromUserDestination);
            messagingTemplate.convertAndSend(toUserDestination, message);
            messagingTemplate.convertAndSend(fromUserDestination, message);
            
            log.info("Message sent successfully from {} to {}", message.getFromId(), message.getToId());
        } catch (Exception e) {
            log.error("Error sending message from {} to {}: {}", message.getFromId(), message.getToId(), e.getMessage(), e);
            throw e;
        }
    }

    // Получить все чаты консультанта (уникальные userId, которые писали консультанту)
    @GetMapping("/consultant/{consultantId}/chats")
    public ChatListResponse getConsultantChats(@PathVariable UUID consultantId) {
        log.info("GET /consultant/{}/chats", consultantId);
        
        try {
            // Все уникальные fromId, которые писали этому консультанту
            List<UUID> chatIds = chatMessageRepository.findByToIdOrderByTimestampDesc(consultantId)
                    .stream().map(ChatMessage::getFromId).distinct().toList();
            
            log.info("Found {} chats for consultant {}", chatIds.size(), consultantId);
            log.debug("Chat IDs: {}", chatIds);
            
            return ChatListResponse.builder()
                    .chatIds(chatIds)
                    .totalChats(chatIds.size())
                    .build();
        } catch (Exception e) {
            log.error("Error getting chats for consultant {}: {}", consultantId, e.getMessage(), e);
            throw e;
        }
    }

    // Получить историю сообщений между консультантом и пользователем
    @GetMapping("/chat/{chatId}")
    public List<ChatMessageResponse> getChatHistory(@PathVariable String chatId) {
        log.info("GET /chat/{}", chatId);
        
        try {
            List<ChatMessage> messages = chatMessageRepository.findByChatIdOrderByTimestampAsc(chatId);
            log.info("Found {} messages for chat {}", messages.size(), chatId);
            
            List<ChatMessageResponse> response = messages.stream()
                    .map(message -> ChatMessageResponse.builder()
                            .id(message.getId())
                            .fromId(message.getFromId())
                            .toId(message.getToId())
                            .content(message.getContent())
                            .timestamp(message.getTimestamp())
                            .chatId(message.getChatId())
                            .build())
                    .collect(Collectors.toList());
            
            log.debug("Returning {} chat message responses", response.size());
            return response;
        } catch (Exception e) {
            log.error("Error getting chat history for chat {}: {}", chatId, e.getMessage(), e);
            throw e;
        }
    }

    // Получить все чаты пользователя (уникальные собеседники)
    @GetMapping("/user/{userId}/chats")
    public ChatListResponse getUserChats(@PathVariable UUID userId) {
        log.info("GET /user/{}/chats", userId);
        
        try {
            List<UUID> chatIds = chatMessageRepository.findByFromIdOrToIdOrderByTimestampDesc(userId, userId)
                .stream()
                .flatMap(msg -> java.util.List.of(msg.getFromId(), msg.getToId()).stream())
                .filter(id -> !id.equals(userId))
                .distinct()
                .toList();
            
            log.info("Found {} chats for user {}", chatIds.size(), userId);
            log.debug("Chat IDs: {}", chatIds);
            
            return ChatListResponse.builder()
                    .chatIds(chatIds)
                    .totalChats(chatIds.size())
                    .build();
        } catch (Exception e) {
            log.error("Error getting chats for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
} 