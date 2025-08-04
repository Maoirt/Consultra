#!/bin/bash

echo "=== Environment Variables Debug ==="
echo "AUTH_SPRING_PROFILES_ACTIVE: $AUTH_SPRING_PROFILES_ACTIVE"
echo "AUTH_DATABASE_URL: $AUTH_DATABASE_URL"
echo "AUTH_DATABASE_USERNAME: $AUTH_DATABASE_USERNAME"
echo "AUTH_DATABASE_PASSWORD: $AUTH_DATABASE_PASSWORD"
echo "AUTH_NOTIFICATION_SERVICE_URL: $AUTH_NOTIFICATION_SERVICE_URL"
echo "AUTH_CORS_ALLOWED_ORIGINS: $AUTH_CORS_ALLOWED_ORIGINS"
echo "AUTH_JWT_SECRET_KEY: $AUTH_JWT_SECRET_KEY"
echo "AUTH_PORT: $AUTH_PORT"
echo "PORT: $PORT"
echo "================================"

# Test database connection
echo "Testing database connection..."
nc -zv db 5432 || echo "Database connection failed"

# Test notification service
echo "Testing notification service..."
nc -zv notification-service 8081 || echo "Notification service connection failed"

# Test if auth service is running
echo "Testing auth service health..."
curl -f http://localhost:8080/actuator/health || echo "Auth service health check failed" 