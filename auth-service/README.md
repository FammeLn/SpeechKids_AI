# 🔐 Auth Service (Halfi Core)

Этот микросервис отвечает за безопасность, регистрацию пользователей, аутентификацию (JWT) и управление паролями. Он напрямую общается с **PostgreSQL** и отправляет сообщения в **Kafka** для асинхронных уведомлений (отправки писем).

---

## 📡 Основные Эндпоинты (REST API)
Все запросы маршрутизируются через API Gateway. Базовый URL для мобильного приложения или фронтенда: 
👉 `http://localhost:8081/api/auth`

| Метод | Эндпоинт | Описание | Тело запроса (JSON) / Параметры |
|---|---|---|---|
| `POST` | `/register` | Регистрация нового аккаунта | `{"email": "my@email.com", "password": "123"}` |
| `POST` | `/login`| Вход в систему и получение JWT | `{"email": "my@email.com", "password": "123"}` |
| `POST` | `/verify` | Активация кода с почты | **Params:** `?email=my@email.com&code=1234`<br/>*(Важно: если передаете через URL строку вручную, символ `+` нужно кодировать как `%2B`)* |
| `POST` | `/forgot-password` | Запрос на сброс забытого пароля | `{"email": "my@email.com"}` |
| `POST` | `/reset-password` | Установка нового пароля по коду | `{"email": "my@email.com", "code": "1234", "newPassword": "new"}` |
| `POST` | `/resend-code`| Запросить повторное письмо на почту | `{"email": "my@email.com"}` |

### 🛡 Защита от спама (Rate Limiting)
Эндпоинты `register`, `forgot-password` и `resend-code` защищены алгоритмом *Exponential Backoff*. 
Если запросить код слишком много раз подряд, сервер вернет **429 Too Many Requests**, попросив подождать 10, 20, 30 или максимум 60 секунд.

---

## 🚀 Как запустить через Docker

Auth Service должен запускаться в локальной подсети `halfi_network` (вместе с базой данных и брокером Kafka).

### 1. Сборка JAR-файла
Если вы внесли изменения в Kotlin-код, перекомпилируйте:
```powershell
.\gradlew.bat :auth-service:clean :auth-service:bootJar -x test
```

### 2. Запуск контейнера
Единый скрипт для запуска (рекомендуется):
```bash
bash docker/setup.sh
```
Или вручную (только для этого сервиса):
```powershell
docker compose -f docker/apps/docker-compose.auth.yml up --build -d
```

### 3. Остановка
```powershell
docker compose -f docker/apps/docker-compose.auth.yml down
```

---

## 🏗 Архитектура и Технологии внутри сервиса
- **База данных**: PostgreSQL (таблицы `users` и `email_attempts`). Hibernate генерирует/обновляет схему автоматически по настройке `ddl-auto: update`.
- **Токены**: JWT Access-токен возвращается прямо в JSON-ответе (строкой), а Refresh-токен дополнительно прокидывается в куки, чтобы браузер/клиент мог безопасно его хранить.
- **Брокер сообщений**: Сообщения пробрасываются в Kafka (в топик `email.send.verification`).
- **Стек**: Spring Boot 3.3.5, Kotlin (с обязательным плагином spring), Spring Security, PasswordEncoder (BCrypt).
