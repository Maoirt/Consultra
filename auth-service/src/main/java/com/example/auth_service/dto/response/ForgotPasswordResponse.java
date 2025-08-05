package com.example.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ на запрос сброса пароля")
public class ForgotPasswordResponse {
    
    @Schema(description = "Сообщение о результате операции")
    private String message;
    
    @Schema(description = "Успешность операции")
    private boolean success;
} 