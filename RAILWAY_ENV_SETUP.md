# Настройка переменных окружения в Railway

## Проблема
Railway предоставляет только одну вкладку для переменных окружения на весь проект, но у нас есть несколько сервисов с одинаковыми названиями переменных.

## Решение: Использование префиксов

Мы используем префиксы для разделения переменных разных сервисов:

### Auth Service (AUTH_*)
- `AUTH_SPRING_PROFILES_ACTIVE=prod`
- `AUTH_DATABASE_URL=jdbc:postgresql://db:5432/consultra`
- `AUTH_DATABASE_USERNAME=postgres`
- `AUTH_DATABASE_PASSWORD=maoirt`
- `AUTH_NOTIFICATION_SERVICE_URL=http://notification-service:8081`
- `AUTH_CORS_ALLOWED_ORIGINS=https://your-frontend-domain.railway.app,http://localhost:3000`
- `AUTH_JWT_SECRET_KEY=your-secret-key-here`
- `AUTH_PORT=8080`

### Notification Service (NOTIFICATION_*)
- `NOTIFICATION_SPRING_PROFILES_ACTIVE=prod`
- `NOTIFICATION_EMAIL_USERNAME=your-email@gmail.com`
- `NOTIFICATION_EMAIL_PASSWORD=your-app-password`
- `NOTIFICATION_EMAIL_HOST=smtp.gmail.com`
- `NOTIFICATION_EMAIL_PORT=587`
- `NOTIFICATION_PORT=8081`

### Frontend (FRONTEND_*)
- `FRONTEND_REACT_APP_API_URL=https://your-backend-domain.railway.app`
- `FRONTEND_REACT_APP_WS_URL=wss://your-backend-domain.railway.app`
- `FRONTEND_PORT=3000`

### Database (DB_*)
- `DB_POSTGRES_DB=consultra`
- `DB_POSTGRES_USER=postgres`
- `DB_POSTGRES_PASSWORD=maoirt`
- `DB_PORT=5432`

## Как настроить в Railway

1. Перейдите в ваш проект в Railway
2. Откройте вкладку "Variables"
3. Добавьте все переменные с префиксами как показано выше
4. Замените placeholder значения на реальные:
   - `your-email@gmail.com` → ваш реальный email
   - `your-app-password` → пароль приложения Gmail
   - `your-secret-key-here` → секретный ключ для JWT
   - `your-frontend-domain.railway.app` → ваш домен frontend в Railway
   - `your-backend-domain.railway.app` → ваш домен backend в Railway

## Альтернативные решения

### Способ 2: Использование разных проектов
Создайте отдельные проекты в Railway для каждого сервиса:
- Проект 1: Auth Service
- Проект 2: Notification Service  
- Проект 3: Frontend
- Проект 4: Database

### Способ 3: Использование .env файлов
Создайте отдельные .env файлы для каждого сервиса и загрузите их в Railway.

## Проверка
После настройки переменных проверьте:
1. Все сервисы запускаются без ошибок
2. База данных подключается
3. Email отправляется
4. Frontend может подключиться к backend 