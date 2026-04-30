# Profile Service

`profile-service` отвечает за хранение и управление обобщенной информацией пользователя (nickname, номер телефона, аватарка, история баланса). Он является частью инфраструктуры `Halfi_core` и взаимодействует с `auth-service` через Kafka и внутренние REST API вызовы.

## Зоны ответственности
- Хранение публичной и приватной информации профиля.
- Автоматическое создание профиля при регистрации нового подтвержденного пользователя (используя Kafka + REST синхронизацию).
- Отдача профиля фронтенду.

## Как запустить локально
Убедитесь, что инфраструктурные сервисы (Kafka, PostgreSQL) запущены из `halfi_core/docker/base/...`.

Запуск через Gradle:
```bash
./gradlew :profile-service:build
```
Запуск через Docker Compose (собирает образ и поднимает контейнер):
```bash
docker-compose -f docker/apps/docker-compose.profile.yml up --build -d
```

---

## 🧪 Инструкция по тестированию в Postman

Обратите внимание: все внешние запросы должны идти через **API Gateway**, который работает на порту `8081`. 

### 1. Получение профиля (GET)
* **Метод:** `GET`
* **URL:** `http://localhost:8081/api/profile/{userId}`
  *(где `{userId}` — это UUID подтверждённого пользователя из таблицы `auth-service`)*

**Ожидаемый ответ (200 OK):**
```json
{
    "userId": "e7eccbf1-dca8-4e22-bf26-a21efd14e8cc",
    "email": "kim.kvinti@gmail.com",
    "nickname": "kim.kvinti",
    "phoneNumber": null,
    "avatarUrl": null,
    "balance": 0
}
```
*Если пользователя с таким UUID нет, вернётся пустой ответ со статусом `404 Not Found`.*

### 2. Редактирование профиля (PUT)
* **Метод:** `PUT`
* **URL:** `http://localhost:8081/api/profile/{userId}`
* **Headers:** `Content-Type: application/json`
* **Body (raw -> JSON):**
```json
{
    "nickname": "DUO",
    "phoneNumber": "+7-999-111-22-33",
    "avatarUrl": "https://imgur.com/my-avatar.png"
}
```

**Ожидаемый ответ (200 OK):**
```json
{
    "userId": "e7eccbf1-dca8-4e22-bf26-a21efd14e8cc",
    "email": "kim.kvinti@gmail.com",
    "nickname": "DUO",
    "phoneNumber": "+7-999-111-22-33",
    "avatarUrl": "https://imgur.com/my-avatar.png",
    "balance": 0
}
```

### 3. Комплексный тест жизненного цикла (Регистрация -> Профиль)
Сервис настроен на автоматическое создание профиля. Чтобы протестировать это:

1. **Зарегистрируйте нового юзера (POST):**
   - URL: `http://localhost:8081/api/auth/register`
   - Body: `{"email": "postman.test@gmail.com", "password": "Password123!"}`
2. **Подтвердите почту (POST):**
   - URL: `http://localhost:8081/api/auth/verify?email=postman.test@gmail.com&code=ВЗЯТЬ_ИЗ_ЛОГОВ_АНАЛОГА_ПОЧТЫ`
   - *После подтверждения `auth-service` выставит флаг `enabled=true` и отправит событие в Kafka.*
3. **Проверьте создание:**
   - Profile-service поймает Kafka-ивент, самостоятельно обратится к `auth-service` (чтобы валидировать, является ли `enabled=true`), и если всё хорошо — создаст профиль.
   - Сделайте запрос: `GET http://localhost:8081/api/profile/УЗНАЙТЕ_UUID_ИЗ_БАЗЫ` 
   - Вы должны получить успешно созданный профиль с `nickname`, вырезанным из email (в нашем случае `"postman.test"`).
