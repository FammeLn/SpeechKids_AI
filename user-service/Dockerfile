# user-service/Dockerfile
# context в docker-compose: .  (корень проекта)

FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
RUN sed -i 's/\r//' gradlew && chmod +x gradlew

COPY settings.gradle.kts settings.gradle.kts
COPY user-service/build.gradle.kts user-service/build.gradle.kts

RUN ./gradlew :user-service:dependencies --no-daemon --console=plain 2>&1 || true

COPY user-service/src user-service/src

RUN ./gradlew :user-service:bootJar --no-daemon -x test --console=plain

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=builder /app/user-service/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]