@echo off
chcp 65001 > nul
echo =====================================================================
echo           Запуск серверной части SpeechKids AI (Docker)
echo =====================================================================
echo.

:: Проверка наличия Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ОШИБКА] Docker не запущен или не установлен!
    echo Пожалуйста, запустите Docker Desktop и попробуйте снова.
    echo.
    pause
    exit /b 1
)

echo [ИНФО] Сборка и запуск контейнеров (PostgreSQL + FastAPI + Spring Boot)...
echo.
docker-compose up --build

echo.
echo [ИНФО] Работа сервисов завершена.
pause
