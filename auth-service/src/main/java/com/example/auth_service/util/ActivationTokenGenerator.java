package com.example.auth_service.util;

import java.util.UUID;

public class ActivationTokenGenerator {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
