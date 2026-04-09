package com.halfi.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Никнейм не может быть пустым")
    @Size(min = 2, max = 20, message = "Никнейм от 2 до 20 символов")
    private String nickName;

    @NotBlank(message = "Email обязателен")
    @Email(
            regexp = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$",
            message = "Некорректный формат email"
    )
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, message = "Пароль минимум 8 символов")
    private String password;

    // Флаг согласия на промо-рассылку (необязателен, default false)
    private boolean promo = false;
}