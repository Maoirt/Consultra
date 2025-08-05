package com.example.auth_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на сброс пароля")
public class ForgotPasswordRequest {
    
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;
} 