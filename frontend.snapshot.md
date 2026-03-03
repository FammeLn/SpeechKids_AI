# 📦 HALFI — PROJECT SNAPSHOT (Frontend + Backend)
Общая идея

AI-платформа с пользовательскими профилями, балансом и настройками интерфейса.

# -- ⚛ Frontend --

Stack:

React + Vite

CSS variables

dataset на <html>

кастомные hooks

Реализовано:

Sticky Navbar

Hide Navbar (hover reveal)

TopLoadingBar

SettingsPopover (dropdown)

AuthPopover

Система тем (light/dark)

Accent color (4 варианта)

i18n (ru/en)

Анимация смены языка (0.5s)

Анимация смены темы (1s overlay + morph)

# --☕ Backend --

Stack:

Java (Spring Boot)

JWT (access 15m + refresh)

UUID

Роли: USER / ADMIN

Kafka

Kubernetes

# -- Архитектура --

Frontend → API Gateway → Microservices
Kafka — события (регистрация, email, security)
Kubernetes — деплой и масштабирование

# -- Текущий фокус --

UI/UX polish
Анимации
Продакшн-уровень архитектуры