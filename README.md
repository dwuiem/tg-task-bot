# Task Bot Application

## Introduction
Проект представляет собой Telegram-bot - планировщик задач разработанный на микросервисной архитекторе с использованием **Spring Boot**

## Main features
- **Spring Boot 3.4 (OpenJDK 17)** 
- **MongoDB (Spring Data MongoDB)** - NoSQL база данных для хранения информации о пользователях, задачах и состояний (ORM взаимодействие)
- **RabbitMQ (Spring for RabbitMQ)** - Обмен сообщениями между **микросервисами** c гарантированной доставкой при сбоях, создания напоминаний при помощи плагина `rabbitmq_delayed_message_exchange`
- **telegrambots API** - Взаимодействие с пользователем посредством интерфейса чат бота
- **Log4j** - логирование
- **Lombok** - генерация шаблонного кода с помощью аннотаций
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
TELEGRAM_BOT_NAME=<имя бота>
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
├── Dockerfile # Для RabbitMQ
├── docker-compose.yml
└── .env
```
1. `telegram-bot-service` - Сервис для взаимодействия с Telegram API, отправки сообщений в очередь
2. `core-service` - Сервис для обработки сообщений, взаимодействие с MongoDB

## MongoDB collections

В проекте используются три основные коллекции в базе данных MongoDB: users, tasks, и reminders. Каждая коллекция представляет собой различные сущности.

Коллекция `users` хранит информацию о пользователях, которые взаимодействуют с Telegram ботом.
```json
{
  "_id": "ObjectId('605c72ef15320736b63a89d1')",
  "chatId": 1234567890, 
  "userState": "NONE_STATE",
  "selectedTaskId": "ObjectId('605c72ef15320736b63a89d2')"
}
```

Документ `tasks` хранит задачи пользователей. Каждая задача может иметь несколько напоминаний.

```json
{
  "_id": "ObjectId('605c72ef15320736b63a89d2')",
  "userId": "ObjectId('605c72ef15320736b63a89d1')",
  "description": "Finish the project",
  "deadline": "2025-03-25T10:00:00",
  "created": "2025-03-20T09:00:00",
  "completed": false
}
```

Коллекция `reminders` хранит напоминания для задач. Каждое напоминание привязано к конкретной задаче.

```json
{
  "_id": "ObjectId('605c72ef15320736b63a89d3')",
  "reminderTime": "2025-03-24T09:00:00",
  "createdAt": "2025-03-20T09:00:00",
  "taskId": "ObjectId('605c72ef15320736b63a89d2')"
}
```

## RabbitMQ Queues

- `message` - поступающие обычные сообщения из telegram-bot-service
- `callback_message` - события callback (нажатия на кнопки)
- `answer_message` - ответные сообщений из core-service
- `delete_message` - удаление сообщений
- `reminders` - напоминания куда попадают сообщения спустя некоторый delay (не через TTL т.к. RabbitMQ не всегда удаляет сообщения вворемя)

## Application features

Так как большую часть взаимодействия происходит через кнопки, комманд всего 3 и их аналоги есть на кнопках клавитауры пользователя
- `/add` - Добавить задачу в формате описание + дедлайн
- `/list` - Просмотреть полный список задач
- `/list_reminders` - Просмотреть

**Бот также умеет проверять сообщение на ошибки. О неправильных датах, форматах ввода, уже удалённых задачах или напоминаниях он будет говорить пользователю**

### 1. Просмотр задач (/list или кнопка "Список всех задач")

Пользователю предоставляется полный список задач **отсортированных по времени дедлайна** вместе с информацией о их выполнении, просроке, дедлайне и количестве напоминаниях

![image](https://github.com/user-attachments/assets/694372ce-6952-49d4-9c05-08cc00506fbd)

Также существует возможность удалить задачи на конкретную дату через кнопку и последующим вводом даты

![image](https://github.com/user-attachments/assets/31936676-ce5a-43bf-82b1-68b58eeb3dfd)


### 2. Добавить задачу (/add или кнопка "Добавить задачу")

После ввода команды пользователь вводит задачу в формате описание + дата и/или время. Если пользователь ввёл только дату, время будет автоматически 23:59, а если только время то дата сегодняшняя

![image](https://github.com/user-attachments/assets/d371fba2-a186-4876-9e89-71185916a0ec)

### 3. Просмотр задач

При выводе списка, под ним появляются кнопки с номерами каждая из которых отвечает за просмотр задачи под её соответствующим номером. Просмотр задачи также необходим чтобы работать с ней посредством интерфейса кнопок

![image](https://github.com/user-attachments/assets/352423a7-b11a-4ed3-bb44-6f8975e2ab92)

### 4 Взаимодействие с задачей

- Удаление задачи происходит по нажатию кнопки Удалить. При попытке обратиться к задаче из истории чата бот выдаст ошибку ❗️ Задача не найдена. Возможно она была удалена

![image](https://github.com/user-attachments/assets/d23d5e94-80a3-4cb2-8f7b-ef586b9df41f)
![image](https://github.com/user-attachments/assets/6196055a-9d7a-4aea-855c-7c6e09279762)

- Чтобы выполнить задачу нужно нажать на галочку, также чтобы отменить выполнение нажать соответствующую кнопку\

![image](https://github.com/user-attachments/assets/ce91e104-fcab-4dea-a97f-a3c966ecd796)
![image](https://github.com/user-attachments/assets/af47824a-7ce4-4988-b418-1c6e5d2ce736)

- Убрать дедлайн у задачи можно по нажатии кнопки

![image](https://github.com/user-attachments/assets/14b5b9f0-c6a0-42d1-bc85-b081a9ab6859)
![image](https://github.com/user-attachments/assets/1f787cee-8d92-4dc6-84cf-99c6e7aa424f)

- Изменить дедлайн и описание можно также по нажатию кнопки и последующего ввода с клавиатуры

![image](https://github.com/user-attachments/assets/df2f19f4-6e04-4d5e-8c05-de74186c333d)
![image](https://github.com/user-attachments/assets/4eead629-45aa-4140-b966-dc9a428aac05)

- Добавить напоминание можно с помощью соответствующей кнопки, далее бот предложит написать время задачи не раньше текущего и не позже времени дедлайна. Напоминание придёт по московскому времен

![image](https://github.com/user-attachments/assets/32f1c0a7-c05b-4eb1-a14c-95af277dfee1)

### 5. Список напоминаний (/list_reminders или кнопка "Напоминания")

Список выводит напоминания с привязкой к конкретной задачи а также кнопки, с помощью которых можно быстро удалить ненужное напоминание

![image](https://github.com/user-attachments/assets/7ebcea7a-5c1f-4334-9138-1c5f58789c39)
![image](https://github.com/user-attachments/assets/36fc6b6c-a08e-4612-b182-6ff9d9a6526d)



