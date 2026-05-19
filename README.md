# SpeechKids_AI
Spring Boot микросервис для БД и базовых API SpeechKids AI.

## Быстрый старт
1. Поднять PostgreSQL:
`docker compose up -d`

2. Запуск приложения:
`mvn spring-boot:run`

По умолчанию сервис доступен на `http://localhost:8080/api`.

## Переменные окружения
- `SPRING_DATASOURCE_URL` (по умолчанию `jdbc:postgresql://localhost:5432/speechkids`)
- `SPRING_DATASOURCE_USERNAME` (по умолчанию `speechkids`)
- `SPRING_DATASOURCE_PASSWORD` (по умолчанию `speechkids`)
- `JWT_SECRET` (минимум 32 символа)
- `JWT_ACCESS_MINUTES` (по умолчанию `60`)
- `JWT_REFRESH_DAYS` (по умолчанию `14`)
- `AUDIO_STORAGE_DIR` (по умолчанию `./data/audio`)

## Seed пользователи
- `parent@test.com` / `password123` (PARENT)
- `therapist@test.com` / `password123` (THERAPIST)
- `admin@test.com` / `password123` (SUPER_ADMIN)

## Основные эндпоинты
- `GET /api/health`
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `GET /api/auth/me`
- `POST /api/children`
- `GET /api/children`
- `GET /api/children/{childId}`
- `GET /api/children/{childId}/sessions`
- `GET /api/children/{childId}/recommendations`
- `GET /api/exercises`
- `GET /api/exercises/{exerciseId}`
- `GET /api/exercises/{exerciseId}/items`
- `POST /api/sessions/start`
- `POST /api/audio/analyze` (multipart/form-data)
- `POST /api/sessions/{sessionId}/finish`
- `GET /api/children/{childId}/progress`
- `GET /api/children/{childId}/phonemes`
- `GET /api/notifications`
- `GET /api/admin/system-health`
- `GET /api/admin/users`
