# 🐳 Инструкция по работе с Docker (Halfi Core)

В этой папке находятся все конфигурации для развертывания проекта.

## 🚀 Быстрый запуск (BASH / Git Bash)
Если у вас установлен Git Bash, используйте готовый скрипт:
```bash
bash docker/setup.sh
```

---

## 🛠 Ручной запуск (Windows PowerShell / CMD)

Если вы запускаете команды по отдельности в PowerShell, используйте следующий порядок:

### 1. Подготовка сети (Один раз)
```powershell
docker network create halfi_network
```

### 2. Сборка всех JAR-файлов
```powershell
.\gradlew.bat bootJar -x test
```

### 3. Запуск Базы данных
```powershell
docker-compose -f docker/base/docker-compose.postgres.yml up -d
```

### 4. Запуск Сервисов
```powershell
# API Gateway
docker-compose -f docker/apps/docker-compose.apigateway.yml up --build -d

# Auth Service
docker-compose -f docker/apps/docker-compose.auth.yml up --build -d
```
### 5. Просмотр таблицы 
docker exec -it postgres psql -U halfi -d halfi_db
--- SELECT * FROM users;

## 🧹 Очистка и удаление
Чтобы остановить всё и **полностью удалить данные базы** (сброс):
```powershell
docker-compose -f docker/apps/docker-compose.auth.yml down
docker-compose -f docker/apps/docker-compose.apigateway.yml down
docker-compose -f docker/base/docker-compose.postgres.yml down -v
```

##  Полезные советы
- **Логи**: `docker logs -f auth-service` (просмотр логов в реальном времени).
- **Порты**: 
  - API Gateway: `8081`
  - Auth Service: `8084` (прямой доступ)
  - Postgres: `5432`
