# Cloud Storage Service

REST-сервис для загрузки и хранения файлов с авторизацией.

## Функциональность

- Авторизация пользователей
- Загрузка файлов
- Скачивание файлов
- Удаление файлов
- Переименование файлов
- Получение списка файлов

## Технологии

- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- PostgreSQL
- JUnit 5 + Mockito
- Testcontainers
- Docker + Docker Compose

## Запуск приложения

### Локальный запуск

1. Запустите PostgreSQL:
```
docker-compose up postgres -d

#### Тестовые пользователи
Логин : user1, Пароль: password1

Логин: user2, Пароль: password2

API Endpoints
POST /login - авторизация

POST /logout - выход

POST /file - загрузка файла

GET /file - скачивание файла

DELETE /file - удаление файла

PUT /file - переименование файла

GET /list - список