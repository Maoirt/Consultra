package com.example.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные для регистрации")
public class SignUpDto {

    @NotBlank
    @Email
    @Size(min = 1, max = 50)
    @Schema(description = "Почта пользователя", example = "user@example.com")
    private String email;

    @NotBlank
    @Size(min = 1, max = 50)
    @Schema(description = "Имя пользователя", example = "Daniel")
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 50)
    @Schema(description = "Фамилия пользователя", example = "Daniel")
    private String lastName;

    @NotBlank
    @Size(min = 1, max = 50)
    @Schema(description = "Телефон пользователя", example = "7918578378")
    private String phone;

    @NotBlank
    @Size(min = 10, max = 250)
    @Schema(description = "Пароль пользователя", example = "r4hoiahiugy3ya8o7gak")
    private char[] password;

    @Schema(description = "Роль пользователя (USER или CONSULTANT)", example = "CONSULTANT")
    private String role;
}
