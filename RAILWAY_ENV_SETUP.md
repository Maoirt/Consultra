# Настройка переменных окружения в Railway

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
- `NOTIFICATION_EMAIL_SUBJECT=Email Verification`
- `NOTIFICATION_EMAIL_MESSAGE=Click the link to verify your email`
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
