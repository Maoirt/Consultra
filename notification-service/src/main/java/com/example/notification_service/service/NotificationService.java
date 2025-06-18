package com.example.notification_service.service;

public interface NotificationService {
    boolean sendVerificationEmail(String email, String code);
} 