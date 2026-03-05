# Halfi Core Backend
нужно скачать плагин lombok
## 📂 Структура проекта

``` 
java/com.Halfi_core
├── 📂 config          # Конфигурации (Security, Kafka, CORS)
├── 📂 controller      # REST контроллеры (API) прием HTTP-запросов
├── 📂 dto             # Data Transfer Objects (классы для проверок)
│   ├── 📂 request    # Входящие (RegisterRequest)
│   └── 📂 response   # Ответы (AuthResponse)
├── 📂 exception       # Обработка ошибок
├── 📂 messaging       # Все, что связано с Kafka
│   ├── 📂 producer   # Отправка сообщений
│   └── 📂 dto        # Объекты событий (например, UserRegisteredEvent)
├── 📂 model           # Сущности (то, что лежит в таблицах БД)).
├── 📂 repository      # Работа с базой данных. Интерфейсы БД (JPA) 
├── 📂 service         # Бизнес-логика
└── BackendApplication # запуск сервера
```
Система аутентификации и управления пользователями с асинхронным подтверждением почты через Kafka.

## 🛠 Технологический стек

* **Backend:** Java 21, Spring Boot 3.x, Spring Security
* **Database:** PostgreSQL 15
* **Messaging:** Apache Kafka (KRaft mode)
* **Infrastructure:** Docker Compos

🏗 Архитектура проекта

Проект реализован на основе многослойной архитектуры (Layered Architecture):

* **Controller**: Прием HTTP запросов и валидация DTO.
* **Service**: Бизнес-логика, хеширование паролей, оркестрация событий.
* **DTO**: Объекты передачи данных (Request/Response) с валидацией `jakarta.validation`.
* **Messaging**: Асинхронная отправка уведомлений через Kafka Producer.
* **Repository**: Слой доступа к данным (Spring Data JPA).

Порты:

PostgreSQL: localhost:5433

Kafka: localhost:9092

Backend: localhost:8084

Frontend: localhost:5173

⚙️ Настройка окружения (Environment)
Основные параметры в application.yml:
b

spring.kafka.bootstrap-servers: localhost:9092

server.port: 8084

Основные эндпоинты API

Метод,Эндпоинт,Описание
POST,/api/v1/auth/register,Регистрация нового пользователя
POST,/api/v1/auth/login,Вход и получение токена/сессии
POST,/api/v1/auth/recover,Запрос на восстановление пароля


