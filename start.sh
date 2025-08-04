#!/bin/bash

echo "🚀 Запуск Consultra локально..."

# Проверяем наличие Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker не установлен. Установите Docker и попробуйте снова."
    exit 1
fi

# Проверяем наличие Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose не установлен. Установите Docker Compose и попробуйте снова."
    exit 1
fi

echo "✅ Docker и Docker Compose найдены"

# Останавливаем существующие контейнеры
echo "🛑 Останавливаем существующие контейнеры..."
docker-compose down

# Запускаем сервисы
echo "🔧 Запускаем сервисы..."
docker-compose up -d

echo "⏳ Ожидаем запуска сервисов..."
sleep 10

# Проверяем статус
echo "📊 Статус сервисов:"
docker-compose ps

echo ""
echo "🎉 Consultra запущена!"
echo ""
echo "📱 Доступные сервисы:"
echo "   Frontend: http://localhost:3000"
echo "   Auth Service: http://localhost:8080"
echo "   Notification Service: http://localhost:8081"
echo "   Database: localhost:5432"
echo ""
echo "🛑 Для остановки выполните: docker-compose down" 