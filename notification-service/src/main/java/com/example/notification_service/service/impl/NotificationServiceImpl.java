package com.example.notification_service.service.impl;

import com.example.notification_service.service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Override
    public boolean sendVerificationEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(email);
            helper.setSubject("Email Verification Code");
            helper.setText("Your verification code is: " + code);
            
            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", email);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", email, e);
            return false;
        }
    }
} 