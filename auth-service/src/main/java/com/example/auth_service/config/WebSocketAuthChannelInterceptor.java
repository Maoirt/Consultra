package com.example.auth_service.config;

import com.example.auth_service.security.UserAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import com.example.auth_service.dto.UserDto;
import org.springframework.messaging.simp.stomp.StompCommand;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final UserAuthProvider userAuthProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Устанавливаем Principal для всех команд, если он ещё не установлен
        if (accessor.getUser() == null) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) userAuthProvider.validateToken(token);
                    String userId = ((com.example.auth_service.dto.UserDto) authentication.getPrincipal()).getId().toString();
                    System.out.println("WebSocket setUser for command " + accessor.getCommand() + " userId: " + userId);
                    accessor.setUser(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userId, null, authentication.getAuthorities()));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid JWT token for WebSocket");
                }
            }
        }
        return message;
    }
} 