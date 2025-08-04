# Изменения для деплоя на Railway.app

## Основные изменения

### 1. Удаление хардкод ссылок

#### Backend (Java):
- Удалены все `@CrossOrigin` аннотации с хардкод ссылками
- Создан `CorsConfig` для централизованного управления CORS
- Добавлена поддержка переменных окружения для всех URL

#### Frontend (React):
- Заменены все хардкод ссылки на переменные окружения
- Добавлена поддержка `REACT_APP_API_URL` и `REACT_APP_WS_URL`
- Создан файл `env.example` с примерами переменных

### 2. Конфигурация переменных окружения

#### Auth Service:
- `DATABASE_URL` - URL базы данных
- `DATABASE_USERNAME` - имя пользователя БД
- `DATABASE_PASSWORD` - пароль БД
- `NOTIFICATION_SERVICE_URL` - URL сервиса уведомлений
- `CORS_ALLOWED_ORIGINS` - разрешенные домены для CORS
- `JWT_SECRET_KEY` - секретный ключ для JWT
- `FRONTEND_URL` - URL фронтенда

#### Notification Service:
- `EMAIL_USERNAME` - email для отправки
- `EMAIL_PASSWORD` - пароль приложения
- `EMAIL_HOST` - SMTP сервер
- `EMAIL_PORT` - SMTP порт

#### Frontend:
- `REACT_APP_API_URL` - URL API
- `REACT_APP_WS_URL` - URL WebSocket

### 3. Файлы для Railway.app

#### Созданные файлы:
- `railway.json` - конфигурация Railway.app
- `railway.toml` - альтернативная конфигурация
- `Dockerfile` - основной Dockerfile
- `.dockerignore` - исключения для Docker
- `README_RAILWAY.md` - инструкции по деплою
- `railway-setup.md` - настройка Railway.app

### 4. Health Check endpoints

#### Добавлены:
- `/actuator/health` - для Railway.app health check
- `/health` - простой health check

### 5. Обновленный docker-compose.yml

#### Изменения:
- Добавлена поддержка переменных окружения
- Настроены порты через переменные
- Добавлены health checks
- Оптимизирована конфигурация для production

## Структура проекта после изменений

```
Auth/Consultra/
├── auth-service/          # Основной сервис
├── notification-service/  # Сервис уведомлений
├── frontend/             # React приложение
├── docker-compose.yml    # Обновленный для Railway
├── Dockerfile           # Основной Dockerfile
├── railway.json         # Конфигурация Railway
├── railway.toml         # Альтернативная конфигурация
├── .dockerignore        # Исключения для Docker
├── README_RAILWAY.md    # Инструкции по деплою
├── railway-setup.md     # Настройка Railway
└── CHANGELOG.md         # Этот файл
```

## Команды для деплоя

### Локальная разработка:
```bash
docker-compose up -d
```

### Деплой на Railway.app:
1. Подключите репозиторий к Railway.app
2. Настройте переменные окружения
3. Запустите деплой

## Проверка после деплоя

1. **Health Check**: `https://your-domain.railway.app/actuator/health`
2. **Frontend**: `https://your-frontend-domain.railway.app`
3. **WebSocket**: Проверьте подключение к чату
4. **Email**: Проверьте отправку уведомлений

## Безопасность

- Все секретные данные вынесены в переменные окружения
- Удалены хардкод ссылки
- Добавлена поддержка HTTPS/WSS для production
- Настроены CORS для безопасности 