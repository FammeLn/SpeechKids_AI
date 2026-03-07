📦 HALFI — PROJECT SNAPSHOT (Frontend + Backend)
Общая идея

AI-платформа с пользовательскими профилями, балансом и настраиваемым интерфейсом.

Фокус проекта — UI/UX-first архитектура и production-ready backend инфраструктура.

⚛ Frontend
Stack

React + Vite

CSS variables

dataset на <html>

кастомные hooks

Lucide icons

Основная архитектура UI

Layout состоит из:

Layout
 ├ Navbar
 ├ TopLoadingBar
 ├ SettingsPopover
 ├ AuthPopover (small)
 ├ RouterView
 └ AuthPopoverShell (modal routes)
Реализованные UI системы
Navbar

Sticky navbar с системой состояний.

Функции:

Sticky header

Hover reveal (Hide Navbar mode)

TopLoadingBar

SettingsPopover

AuthPopover (быстрый логин)

баланс пользователя

аватар пользователя

Hide Navbar

Navbar может скрываться.

Поведение:

Navbar hidden
↓ hover top zone
Navbar appears
↓ mouse leave
Navbar hides

Если открыт popover или auth modal → navbar lock-mode.

Auth System
Маленький popover (navbar)

Быстрый логин:

email
password
login
register
forgot password

Flow:

login success
→ закрыть popover

login error
→ открыть большой auth modal
Большой auth modal

Реализован через modal routes.

/login
/register
/forgot-password

Особенности:

открывается поверх текущей страницы

фон страницы сохраняется

navbar остаётся кликабельным

AuthPopoverShell

Контейнер модалки.

Функции:

backdrop

open / close animation

swap animation между формами

lock navbar

disable body scroll

Auth формы
Login

Поля:

email
password
Register

Поля:

email
password
confirm password
nickname
promo opt-in
agreement

Email verification flow:

send code
enter code
verify
resend
Recover password

Поля:

email
Settings System

SettingsPopover управляет UI настройками.

Хранятся в:

localStorage
halfi_settings_v1
Настройки
locale
theme
accent
hideNavbar
reduceMotion
showBalance
UI Themes

Поддерживается:

light
dark

Переключение сопровождается overlay animation.

Accent Colors
orange
blue
green
red

Реализовано через CSS variables.

i18n

Поддерживается:

ru
en

Смена языка сопровождается fade animation.

UI Animations

Реализовано:

Language switch
fade overlay
500ms
Theme switch
overlay morph
1000ms
Auth modal
open animation
close animation
swap animation
Email code UI
morph transitions
Navbar reveal
hover animation
Accessibility / Motion Control

Поддерживается режим:

reduceMotion

При включении:

отключаются анимации

отключаются transitions

Balance System (UI)

Опциональное отображение баланса в navbar.

[Coins icon] 1250 +

Функции:

отображение баланса

кнопка пополнения (TODO)

Backend
Stack
Java
Spring Boot
JWT auth
UUID
Kafka
Kubernetes
Authentication
JWT access token
refresh token

Access token:

15 minutes
Roles
USER
ADMIN
Backend Architecture
Frontend
   ↓
API Gateway
   ↓
Microservices
   ↓
Kafka Event Bus
Kafka Usage

Используется для событий:

user registration
email verification
security events
notifications
Deployment
Docker
Kubernetes

Поддержка:

горизонтального масштабирования

микросервисной архитектуры

Текущий фокус разработки
UI/UX polish
animations
auth flows
frontend architecture
Ближайшие задачи

Frontend:

API integration
topup page
user profile
notifications

Backend:

auth endpoints
email verification
payments
user profile service
Общая цель проекта

Создать production-ready AI платформу с:

чистой архитектурой

гибким UI

масштабируемым backend

современным UX.