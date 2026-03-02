# Используем легковесную JRE
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY target/reactive-kafka-app.jar app.jar

# Настройки JVM для контейнера
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]