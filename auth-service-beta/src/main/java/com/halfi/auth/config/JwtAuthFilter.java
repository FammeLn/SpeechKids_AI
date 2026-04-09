package com.halfi.auth.config;

import com.halfi.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Фильтр выполняется один раз на каждый запрос (OncePerRequestFilter).
 * Извлекает JWT из заголовка Authorization: Bearer <token>
 * Если токен валиден — устанавливает аутентификацию в SecurityContext.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Извлекаем заголовок Authorization
        String authHeader = request.getHeader("Authorization");

        // Если заголовка нет или не начинается с "Bearer " — пропускаем
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Обрезаем "Bearer " (7 символов)
        String token = authHeader.substring(7);

        // Валидируем токен
        if (jwtService.isTokenValid(token)) {
            String userId = jwtService.extractUserId(token);
            String email = jwtService.extractEmail(token);

            // Устанавливаем аутентификацию в контекст Spring Security
            // principal = userId, credentials = null, authorities = [] (пустые для старта)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,          // principal — userId как String
                            email,           // credentials — email
                            Collections.emptyList() // roles — добавишь потом если нужно
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}