### Общая информация ###

- Node.js: 20+
- Install: `npm i`
- Run: `npm run dev`

## Security ##
- `npm audit --omit=dev` → Должно быть 0 vulnerabilities

🧠 Общая архитектура

На фронте реализовано:

🔹 Маленький popover авторизации (в navbar)

🔹 Большой auth modal (через modal-routes: /login, /register, /forgot-password)

🔹 Настройки пользователя

🔹 Отображение баланса в navbar (по настройке)

🔹 Локализация, темы, анимации

Фронт сам управляет UI-логикой.
Бэкенд должен предоставлять чистые API и корректные коды ошибок.

1️⃣ Авторизация
🔹 Быстрый логин (маленький popover)

Фронт вызывает:

onTryLogin({ email, password })
Ожидаемый формат ответа:
✅ Успех
{
  "ok": true,
  "user": {
    "id": "uuid",
    "email": "user@email.com",
    "avatarUrl": "https://...",
    "balance": 1250
  },
  "tokens": {
    "accessToken": "jwt",
    "refreshToken": "jwt"
  }
}
❌ Ошибка
{
  "ok": false,
  "reason": "credentials" 
}

Допустимые reason:

credentials — неверная пара email+password

email — email не существует / заблокирован

password — неверный пароль

network — ошибка соединения

🔹 Endpoint логина
POST /auth/login

Request:

{
  "email": "string",
  "password": "string"
}
2️⃣ Регистрация

Фронт ожидает 3 этапа:

🔹 2.1 Отправка email-кода
POST /auth/send-email-code

Request:

{
  "email": "string"
}

Response:

{
  "success": true,
  "cooldownSeconds": 60
}
🔹 2.2 Проверка кода
POST /auth/verify-email-code

Request:

{
  "email": "string",
  "code": "123456"
}

Response:

{
  "success": true
}
🔹 2.3 Регистрация
POST /auth/register

Request:

{
  "email": "string",
  "password": "string",
  "nickName": "string",
  "promo": true
}

Response аналогичен login (user + tokens).

3️⃣ Восстановление пароля
POST /auth/forgot-password

Request:

{
  "email": "string"
}

⚠️ Должен всегда возвращать OK (чтобы не раскрывать существование email).

4️⃣ Пользователь
🔹 Получение текущего пользователя
GET /users/me

Header:

Authorization: Bearer <accessToken>

Response:

{
  "id": "uuid",
  "email": "user@email.com",
  "avatarUrl": "https://...",
  "balance": 1250
}
5️⃣ Баланс

Баланс отображается в navbar (если включена настройка).

🔹 Пополнение (TODO)
POST /payments/topup

Request:

{
  "amount": 1000
}

Response:

{
  "success": true,
  "newBalance": 2250
}

Баланс может:

приходить в login/register

приходить через /users/me

6️⃣ Требования к User объекту

Фронту сейчас нужны:

{
  "id": "uuid",
  "email": "string",
  "avatarUrl": "string | null",
  "balance": 0,
  "nickName": "string"
}
7️⃣ Ошибки (единый формат)

Рекомендуемый формат:

{
  "error": "error_code",
  "message": "optional human readable"
}

Фронт маппит error_code → в UI (credentials, email, password).

8️⃣ Токены

Рекомендуется:

accessToken — short lived (15–30 мин)

refreshToken — httpOnly cookie (предпочтительно)

Либо оба в JSON — если без cookie.

9️⃣ ENV

Фронт использует:

VITE_API_URL=https://api.domain.com
🔟 Важно по роутингу

Фронт использует modal-routes:

/login

/register

/forgot-password

Они открываются поверх текущей страницы.
Бэкенд не должен делать принудительные редиректы, фронт сам управляет переходами.