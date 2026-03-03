Контейнер: halfi-postgres
Внешний порт: 5433 (внутри 5432)
База данных: halfi_user_db
Пользователь: HalfiPostgres

при смене порта для postgresql надо изменить также данные в application.properties
spring.jpa.hibernate.ddl-auto=update, Spring сам создает и обновляет таблицы (например, таблицу users) при запуске.

Контроллер: Готов принимать POST запросы от фронтенда на http://localhost:8084/api/v1/auth/register. (frontend/src/api/auth.js)



База данных (Структура)
id (автогенерация)
userName
email (уникальный — база не даст создать двух юзеров на одну почту)
password


Зайти в базу вручную  посмотреть данные
docker exec -it halfi-postgres psql -U HalfiPostgres -d halfi_user_db
SELECT * FROM users;
