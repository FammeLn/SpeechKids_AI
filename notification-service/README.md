# 📬 Notification Service (Halfi Core)

Это классический асинхронный микросервис-воркер, который отвечает исключительно за отправку красивых HTML-писем вашим пользователям. Внутри он подписан (слушает) топик в **Kafka** и связывается напрямую с **SMTP серверами Google**.

---

## 📡 Логика работы (Kafka Consumer)
Этот сервис **не имеет** открытых REST API (у него нет контроллеров для HTTP запросов). Он работает невидимо "под капотом":

1. Постоянно слушает топик `email.send.verification` в Kafka-кластере.
2. Когда Auth Service выкидывает сообщение, Notification Service мгновенно его считывает (обычно это длится миллисекунды).
3. В зависимости от переданного поля `type` ("REGISTRATION" или "RESET_PASSWORD") выбирается соответствующий HTML-шаблон. Заголовки и тексты кнопок генерируются динамически.
4. Отправляет письмо пользователю через `JavaMailSender`.

---

## 🚀 Как запустить через Docker

### 1. Сборка JAR-файла
Если вы поменяли дизайн HTML-шаблона или обновили Kotlin-код, обязательно сделайте чистую сборку:
```powershell
.\gradlew.bat :notification-service:clean :notification-service:bootJar -x test
```

### 2. Запуск контейнера
Этот сервис **требует**, чтобы Kafka внутри вашей системы уже запустилась, иначе он будет кидать Connection Exceptions.

Запуск через общий скрипт (он сам подождет пока Kafka включится):
```bash
bash docker/setup.sh
```
Или вручную:
```powershell
docker compose -f docker/apps/docker-compose.notification.yml up --build -d
```

### 3. Остановка
```powershell
docker compose -f docker/apps/docker-compose.notification.yml down
```

---

## 🔧 Настройка SMTP (Почтовый сервер)
Модуль использует почту Gmail и **App Password** (Пароль приложения) для обхода двухфакторной аутентификации.

Учетные данные не хранятся в открытом виде в `application.yml` (так как это небезопасно для гита). В Docker-окружении они передаются в контейнер через Environment-переменные в самом docker-compose:
- `EMAIL_USERNAME=ваша_почта@gmail.com`
- `EMAIL_PASSWORD=ваш_пароль_приложения`

## 🩺 Логирование и Дебаг
Если пользователь жалуется, что "письмо не пришло", выявить причину можно, почитав логи этого сервиса:
```powershell
docker logs -f notification-service
```
Ожидаемый лог при успешной отправке: 
`KafkaConsumer: Received message... -> Email sent to xxxx@gmail.com (Type: REGISTRATION)`
