# Настройка Railway.app

## Шаги для деплоя:

### 1. Подготовка репозитория
- Убедитесь, что все изменения закоммичены и запушены в GitHub
- Проверьте, что все файлы конфигурации на месте

### 2. Создание проекта в Railway.app
1. Зайдите на [railway.app](https://railway.app)
2. Создайте новый проект
3. Подключите ваш GitHub репозиторий
4. Выберите ветку для деплоя

### 3. Настройка переменных окружения

#### Основные переменные:
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://db:5432/consultra
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-secure-password
NOTIFICATION_SERVICE_URL=http://notification-service:8081
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.railway.app,http://localhost:3000
JWT_SECRET_KEY=your-secure-jwt-secret
FRONTEND_URL=https://your-frontend-domain.railway.app
```

#### Email настройки:
```
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
```

#### Frontend переменные:
```
REACT_APP_API_URL=https://your-backend-domain.railway.app
REACT_APP_WS_URL=wss://your-backend-domain.railway.app
```

### 4. Настройка сервисов

#### Auth Service:
- Порт: 8080
- Health check: `/actuator/health`

#### Notification Service:
- Порт: 8081
- Health check: `/actuator/health`

#### Frontend:
- Порт: 3000
- Build command: `npm run build`

#### Database:
- PostgreSQL
- Порт: 5432

### 5. Проверка деплоя

После деплоя проверьте:
1. Доступность auth-service: `https://your-domain.railway.app/actuator/health`
2. Доступность frontend: `https://your-frontend-domain.railway.app`
3. Работу WebSocket соединений
4. Отправку email уведомлений

### 6. Мониторинг

Используйте Railway.app Dashboard для:
- Просмотра логов
- Мониторинга производительности
- Управления переменными окружения
- Перезапуска сервисов

### 7. Обновления

Для обновления:
1. Внесите изменения в код
2. Запушьте в GitHub
3. Railway.app автоматически пересоберет и перезапустит приложение

## Устранение проблем

### Проблемы с CORS:
- Проверьте переменную `CORS_ALLOWED_ORIGINS`
- Убедитесь, что домены указаны правильно

### Проблемы с базой данных:
- Проверьте переменные `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- Убедитесь, что база данных запущена

### Проблемы с email:
- Проверьте настройки SMTP
- Убедитесь, что `EMAIL_PASSWORD` - это app password, а не обычный пароль

### Проблемы с WebSocket:
- Убедитесь, что используется `wss://` для production
- Проверьте переменную `REACT_APP_WS_URL` 