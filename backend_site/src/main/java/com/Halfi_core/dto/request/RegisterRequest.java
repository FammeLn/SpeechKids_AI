package com.Halfi_core.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Никнейм не может быть пустым")
    @Size(min = 2, max = 20, message = "Никнейм должен быть от 3 до 20 символов")
    private String nickname;

    @NotBlank(message = "Email обязателен")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Некорректный формат почты")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    private String password;
}
