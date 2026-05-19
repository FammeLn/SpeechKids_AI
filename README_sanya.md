# SpeechKids DB Service — как это работает

Этот микросервис хранит пользователей/детей/сессии, выдает упражнения, принимает аудио-попытки и агрегирует прогресс. Реализован на Spring Boot 3.3, PostgreSQL + JPA, миграции Flyway, аутентификация — JWT.

## Архитектура в двух словах
HTTP слой — `@RestController` в `src/main/java/com/speechkids/controller`, бизнес-логика — `service`, данные — `entity` + `repository`. Все ответы, кроме `204`, — JSON. Аудио-отправка — `multipart/form-data`.

## Аутентификация и доступ
1. `POST /api/auth/register` — регистрация и выдача токенов.
2. `POST /api/auth/login` — логин и выдача токенов.
3. `POST /api/auth/refresh` — обмен refresh-токена на новую пару.
4. `POST /api/auth/logout` — помечает refresh-токен как отозванный.

Доступ к большинству API только с `Authorization: Bearer <accessToken>`. Исключения — `/api/health` и `/api/auth/**`. Админские эндпоинты `/api/admin/**` требуют роль `SUPER_ADMIN` (через `@PreAuthorize`).

## Основной пользовательский поток
1. Родитель/логопед создаёт ребёнка: `POST /api/children`.
2. Клиент получает упражнения и элементы: `GET /api/exercises`, `GET /api/exercises/{id}/items`.
3. Стартует сессию: `POST /api/sessions/start`.
4. Для каждого задания отправляет аудио: `POST /api/audio/analyze` (multipart).
5. Завершает сессию: `POST /api/sessions/{sessionId}/finish`.
6. Смотрит прогресс и рекомендации: `GET /api/children/{childId}/progress`, `/phonemes`, `/recommendations`.

## Что делает `/api/audio/analyze`
`AudioService.analyze(...)`:
- проверяет наличие аудио и валидирует `childId/sessionId/exerciseItemId`;
- создаёт `Attempt` (попытку), проставляет очки, XP, рекомендации;
- сохраняет аудио на диск в `AUDIO_STORAGE_DIR` как `<attemptId>.wav`;
- записывает фонемные оценки в `phoneme_scores`;
- при успехе пишет рекомендацию в `recommendations`;
- возвращает `AnalyzeResultDto` с подробным результатом.

Важно: сейчас «распознавание» — заглушка. `recognizedText` берётся из `targetWord`, а score/XP рассчитываются простыми правилами (см. `AudioService`). Это место для подключения реального ASR/ML.

## Сессии и прогресс
`SessionService`:
- `startSession` создаёт активную сессию со стартовым временем и нулевыми метриками.
- `finishSession` завершает сессию, считает средний score и сумму XP по попыткам.

`ChildService`:
- агрегирует прогресс (уровень по XP, средний score, streak, сильные/слабые фонемы);
- отдаёт рекомендации и историю сессий.

## Уведомления
`NotificationService` выдаёт уведомления пользователя и помечает их прочитанными. Данные хранятся в таблице `notifications`.

## Админский системный статус
`AdminService.getSystemHealth()` возвращает статичный DTO со сведениями о моделях. Это заглушка, которую можно заменить реальными health-check’ами/метриками.

## Данные и миграции
Схема БД создаётся Flyway (`src/main/resources/db/migration/V1__init.sql`). JPA настроен на `ddl-auto: validate`, то есть схема должна соответствовать миграциям.

При пустой БД запускается `DataSeeder`, который создаёт тестовых пользователей, ребёнка, уведомление и упражнения/элементы.

## Хранение аудио
Файлы сохраняются локально в директорию `AUDIO_STORAGE_DIR` (по умолчанию `./data/audio`). URL для аудио сохраняется в `attempts.audio_url`, но endpoint для скачивания аудио сейчас не реализован.