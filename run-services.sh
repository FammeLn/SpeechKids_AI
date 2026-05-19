#!/bin/bash

echo "====================================================================="
echo "          Запуск серверной части SpeechKids AI (Docker)"
echo "====================================================================="
echo ""

# Проверка Docker
if ! [ -x "$(command -v docker)" ]; then
  echo '[ОШИБКА] Docker не установлен!' >&2
  exit 1
fi

echo "[ИНФО] Сборка и запуск контейнеров (PostgreSQL + FastAPI + Spring Boot)..."
echo ""
docker-compose up --build
