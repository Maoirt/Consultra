package com.example.auth_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на установку нового пароля")
public class ResetPasswordRequest {
    
    @Schema(description = "Токен для сброса пароля")
    private String token;
    
    @Schema(description = "Новый пароль", example = "newPassword123")
    private String newPassword;
} 