# Деплой на Railway.app

## Подготовка к деплою

### 1. Переменные окружения

Создайте следующие переменные окружения в Railway.app:

#### Для Auth Service:
- `SPRING_PROFILES_ACTIVE=prod`
- `DATABASE_URL=jdbc:postgresql://db:5432/consultra`
- `DATABASE_USERNAME=postgres`
- `DATABASE_PASSWORD=your-db-password`
- `NOTIFICATION_SERVICE_URL=http://notification-service:8081`
- `CORS_ALLOWED_ORIGINS=https://your-frontend-domain.railway.app,http://localhost:3000`
- `JWT_SECRET_KEY=your-secret-key-here`
- `PORT=8080`

#### Для Notification Service:
- `SPRING_PROFILES_ACTIVE=prod`
- `EMAIL_USERNAME=your-email@gmail.com`
- `EMAIL_PASSWORD=your-app-password`
- `EMAIL_HOST=smtp.gmail.com`
- `EMAIL_PORT=587`
- `PORT=8081`

#### Для Frontend:
- `REACT_APP_API_URL=https://your-backend-domain.railway.app`
- `REACT_APP_WS_URL=wss://your-backend-domain.railway.app`
- `PORT=3000`

#### Для Database:
- `POSTGRES_DB=consultra`
- `POSTGRES_USER=postgres`
- `POSTGRES_PASSWORD=your-db-password`

### 2. Деплой

1. Подключите ваш GitHub репозиторий к Railway.app
2. Выберите ветку для деплоя
3. Настройте переменные окружения
4. Запустите деплой

### 3. Структура сервисов

Проект состоит из следующих сервисов:
- **auth-service**: Основной сервис авторизации (порт 8080)
- **notification-service**: Сервис уведомлений (порт 8081)
- **frontend**: React приложение (порт 3000)
- **db**: PostgreSQL база данных (порт 5432)

### 4. Проверка деплоя

После деплоя проверьте:
1. Доступность auth-service: `https://your-domain.railway.app/actuator/health`
2. Доступность frontend: `https://your-frontend-domain.railway.app`
3. Работу WebSocket соединений
4. Отправку email уведомлений

### 5. Логи

Для просмотра логов используйте Railway.app Dashboard или команду:
```bash
railway logs
```

### 6. Обновление

Для обновления приложения:
1. Внесите изменения в код
2. Запушьте в GitHub
3. Railway.app автоматически пересоберет и перезапустит приложение

## Локальная разработка

Для локальной разработки используйте:
```bash
docker-compose up -d
```

Это запустит все сервисы с локальными настройками. 