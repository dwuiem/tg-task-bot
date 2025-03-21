# Task Bot Application

## Introduction
Проект представляет собой Telegram-bot - планировщик задач разработанный на микросервисной архитекторе с использованием **Spring Boot**

## Main features
- **Spring Boot 3.4 (OpenJDK 17)** 
- **MongoDB (Spring Data MongoDB)** - NoSQL база данных для хранения информации о пользователях, задачах и состояний (ORM взаимодействие)
- **RabbitMQ (Spring for RabbitMQ)** - Обмен сообщениями между **микросервисами** c гарантированной доставкой при сбоях, создания напоминаний при помощи TTL
- **telegrambots API** - Взаимодействие с пользователем посредством интерфейса чат бота, который позволяет:
  - Просматривать/Создавать/Редактировать/Удалять задачи из списка
  - Просмотр истёкших задач
  - Лонгирование задач на 1 день вперёд (в разработке)
  - Отмечать выполненные задачи
  - Устанавливать напоминания на предстоящие задачи (в разработке)
- **Log4j** - логирование
 
- Упаковка микросервисов в **Docker** контейнеры для удобного развертывания

## Usage
1. Клонирование
```bash
git clone https://github.com/username/task-bot.git
cd task-bot
```
2. Настройка переменных окружения

Создайте `.env` файл в корне проекта со следующими переменными
```env
TELEGRAM_BOT_TOKEN=<ваш токен>

RABBIT_MQ_HOST=rabbitmq
RABBIT_MQ_PORT=5672
RABBIT_MQ_USERNAME=<ваш логин>
RABBIT_MQ_PASSWORD=<пароль>

MONGO_DB_URI=mongodb://<логин>:<пароль>@mongodb:27017/<имя бд>?authSource=admin
```
3. Запуск при помощи `docker-compose`
```
docker-compose up -d --build
```

## Project general structure
```
tg-task-bot/
├── telegram-bot-service/
│   ├── src/                    
│   ├── pom.xml                 
│   └── Dockerfile
├── core-service/
│   ├── src/
│   ├── pom.xml           
│   └── Dockerfile
├── docker-compose.yml
└── .env
```
1. `telegram-bot-service` - Сервис для взаимодействия с Telegram API, отправки сообщений в очередь
2. `core-service` - Сервис для обработки сообщений, взаимодействие с MongoDB
