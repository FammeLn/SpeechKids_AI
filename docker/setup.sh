#!/bin/bash
set -e # Остановить скрипт, если любая из команд завершится ошибкой

# 1. Запуск инфраструктуры
echo "Запуск PostgreSQL..."
docker compose -f docker/base/docker-compose.postgres.yml up -d

echo "Запуск Kafka (KRaft)..."
docker compose -f docker/base/docker-compose.kafka.yml up -d

# Пауза, чтобы база и Kafka успели проинициализироваться
echo "Ждем 10 секунд для инициализации инфраструктуры..."
sleep 10

# ПРОВЕРКА: Если Kafka упала, выкидываем исключение и стопаем скрипт!
if [ "$(docker inspect -f '{{.State.Running}}' kafka)" != "true" ]; then
    echo "❌ ОШИБКА: Kafka не запустилась! Проверьте 'docker logs kafka'."
    exit 1
fi

if [ "$(docker inspect -f '{{.State.Running}}' postgres)" != "true" ]; then
    echo "❌ ОШИБКА: Postgres не запустился! Проверьте логи."
    exit 1
fi

# 2. Запуск микросервисов
echo "Сборка и запуск API Gateway..."
docker compose -f docker/apps/docker-compose.apigateway.yml up --build -d

echo "Сборка и запуск Auth Service..."
docker compose -f docker/apps/docker-compose.auth.yml up --build -d

echo "Сборка и запуск Notification Service..."
docker compose -f docker/apps/docker-compose.notification.yml up --build -d

echo "Готово! Все сервисы запущены."
