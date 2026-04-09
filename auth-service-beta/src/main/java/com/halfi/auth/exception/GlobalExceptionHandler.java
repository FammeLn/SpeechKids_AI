package com.halfi.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Перехватывает все исключения и возвращает единый формат:
 * { "error": "error_code", "message": "human readable" }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Наши кастомные ошибки аутентификации.
     * AuthException.reason → поле "error" в ответе.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuthException(AuthException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(errorBody(e.getReason(), e.getMessage()));
    }

    /**
     * Ошибки валидации @Valid — собираем все поля в одно сообщение.
     * Пример: { "error": "validation", "message": "nickName: Никнейм от 2 до 20 символов" }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody("validation", message));
    }

    /**
     * Все остальные непредвиденные ошибки — не раскрываем детали клиенту.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
        // Логируем для себя, фронту — только general
        System.err.println("[ERROR] Unhandled exception: " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("server_error", "Внутренняя ошибка сервера"));
    }

    // ── Хелпер ────────────────────────────────────────────────────────────────

    private Map<String, String> errorBody(String error, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}