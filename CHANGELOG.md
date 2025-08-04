# Изменения для локального запуска

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

### 3. Файлы для локального запуска

#### Созданные файлы:
- `Dockerfile` - основной Dockerfile
- `.dockerignore` - исключения для Docker
- `docker-compose.yml` - конфигурация для локального запуска

### 4. Health Check endpoints

#### Добавлены:
- `/actuator/health` - для health check
- `/health` - простой health check

### 5. Обновленный docker-compose.yml

#### Изменения:
- Упрощена конфигурация для локального запуска
- Убраны Railway-специфичные переменные
- Настроены фиксированные порты
- Оптимизирована конфигурация для разработки

## Структура проекта после изменений

```
Auth/Consultra/
├── auth-service/          # Основной сервис
├── notification-service/  # Сервис уведомлений
├── frontend/             # React приложение
├── docker-compose.yml    # Конфигурация для локального запуска
├── Dockerfile           # Основной Dockerfile
├── .dockerignore        # Исключения для Docker
└── CHANGELOG.md         # Этот файл
```

## Команды для локального запуска

### Локальная разработка:
```bash
docker-compose up -d
```

### Остановка:
```bash
docker-compose down
```

### Пересборка:
```bash
docker-compose up -d --build
```

## Проверка после запуска

1. **Auth Service**: `http://localhost:8080/actuator/health`
2. **Notification Service**: `http://localhost:8081/actuator/health`
3. **Frontend**: `http://localhost:3000`
4. **Database**: `localhost:5432`
5. **WebSocket**: Проверьте подключение к чату
6. **Email**: Проверьте отправку уведомлений

## Безопасность

- Все секретные данные вынесены в переменные окружения
- Удалены хардкод ссылки
- Настроены CORS для безопасности
- Используется профиль `dev` для разработки 