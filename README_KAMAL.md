# Руководство по развертыванию SpeechKids AI с помощью Kamal

[Kamal](https://kamal-deploy.org/) (ранее Kamal Deploy от Basecamp) — это современный инструмент для деплоя контейнеризированных приложений на любые серверы по протоколу SSH с использованием Docker и прокси-сервера Traefik.

Это руководство описывает, как настроить деплой платформы SpeechKids AI (Spring Boot Backend + FastAPI AI-сервис) с помощью Kamal.

---

## Требования для деплоя
1. Установленный **Ruby** на вашем локальном компьютере.
2. Установленный **Kamal CLI**:
   ```bash
   gem install kamal
   ```
3. Аккаунт на Docker Registry (например, Docker Hub или GitHub Container Registry).
4. VPS/сервер с чистой ОС (Ubuntu 22.04 LTS рекомендуется) и SSH-доступом по ключу.

---

## 1. Конфигурация Kamal (`config/deploy.yml`)

В папке проекта создайте структуру `config/deploy.yml` для каждого из сервисов. Ниже приведен пример файла конфигурации для развертывания Spring Boot Backend и сопутствующих сервисов.

### Шаблон `config/deploy.yml` для бэкенда:
```yaml
service: speechkids-backend

# Имя вашего репозитория образов Docker
image: username/speechkids-backend

# IP-адрес вашего сервера (или серверов)
servers:
  web:
    - 192.168.1.100 # Укажите IP вашего VPS

# Настройки реестра контейнеров
registry:
  username: docker_hub_username
  password:
    - KAMAL_REGISTRY_PASSWORD # Переменная берется из файла .env

# Конфигурация прокси-сервера (Traefik)
proxy:
  ssl: false # Если используете Cloudflare или внешний SSL, иначе настройте сертификаты в Traefik
  host: api.speechkids.ru
  port: 8080 # Внутренний порт Spring Boot

# Переменные окружения для Spring Boot контейнера
env:
  clear:
    SPRING_DATASOURCE_URL: jdbc:postgresql://speechkids-db:5432/speechkids
    SPRING_DATASOURCE_USERNAME: postgres
    AI_SERVICE_URL: http://speechkids-ai:8001
  secret:
    - SPRING_DATASOURCE_PASSWORD

# База данных PostgreSQL запускается как "аксессуар" (Accessory)
accessories:
  db:
    image: postgres:15-alpine
    host: 192.168.1.100
    port: "5432:5432"
    env:
      clear:
        POSTGRES_DB: speechkids
        POSTGRES_USER: postgres
      secret:
        - POSTGRES_PASSWORD
    files:
      - db/init.sql:/docker-entrypoint-initdb.d/init.sql
    directories:
      - /var/lib/postgresql/data:/var/lib/postgresql/data

  ai-service:
    image: username/speechkids-ai
    host: 192.168.1.100
    port: "8001:8001"
    env:
      clear:
        WHISPER_MODEL_NAME: base
```

---

## 2. Создание файла локальных секретов (`.env`)

Создайте файл `.env` в корне проекта для хранения паролей, которые Kamal автоматически подставит при сборке и деплое:

```env
KAMAL_REGISTRY_PASSWORD=ваш_пароль_от_docker_hub
SPRING_DATASOURCE_PASSWORD=пароль_к_базе_данных
POSTGRES_PASSWORD=пароль_к_базе_данных
```

---

## 3. Команды для запуска и развертывания

Перед выполнением убедитесь, что образы собраны и запушены в Docker Registry, либо предоставьте Kamal возможность собрать их локально.

### Шаг 1: Первичная настройка сервера (Setup)
Эта команда подготовит сервер: установит Docker (если не установлен), настроит сеть и запустит Traefik:
```bash
kamal setup
```

### Шаг 2: Деплой обновлений
Каждый раз, когда вы вносите изменения в код и хотите обновить сервер:
```bash
kamal deploy
```
*Kamal автоматически соберет образ, загрузит его в ваш Registry, скачает на целевом сервере и выполнит «zero-downtime» перезапуск контейнеров.*

### Шаг 3: Просмотр логов
Если нужно посмотреть логи запущенного приложения на сервере:
```bash
kamal app logs
```

### Шаг 4: Проверка статуса контейнеров
Посмотреть запущенные процессы и аксессуары на сервере:
```bash
kamal details
```

### Шаг 5: Быстрый откат (Rollback)
Если в новой версии обнаружилась критическая ошибка, можно мгновенно откатиться на предыдущий рабочий контейнер:
```bash
kamal rollback [IMAGE_TAG]
```
*(Список доступных тегов можно увидеть через `kamal app containers`)*
