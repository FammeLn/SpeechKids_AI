# auth-service/Dockerfile
# context в docker-compose: .  (корень проекта)

FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# gradlew из корня — конвертируем line endings (Windows CRLF -> Unix LF)
COPY gradlew gradlew
COPY gradle gradle
RUN sed -i 's/\r//' gradlew && chmod +x gradlew

# Настройки проекта
COPY settings.gradle.kts settings.gradle.kts

# Build файл только этого модуля
COPY auth-service/build.gradle.kts auth-service/build.gradle.kts

# Кешируем зависимости
RUN ./gradlew :auth-service:dependencies --no-daemon --console=plain 2>&1 || true

# Исходники
COPY auth-service/src auth-service/src

# Собираем
RUN ./gradlew :auth-service:bootJar --no-daemon -x test --console=plain

# ── Runtime ───────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /app/auth-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]