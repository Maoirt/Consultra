FROM openjdk:17-jdk-slim

WORKDIR /app

# Install Maven, Node.js, npm, nginx and other dependencies
RUN apt-get update && apt-get install -y \
    maven \
    nodejs \
    npm \
    nginx \
    curl \
    netcat \
    && rm -rf /var/lib/apt/lists/*

# Copy the entire project
COPY . .

# Build auth-service
RUN cd auth-service && mvn clean package -DskipTests -q

# Build notification-service
RUN cd notification-service && mvn clean package -DskipTests -q

# Build frontend
RUN cd frontend && npm install && npm run build

# Copy nginx configuration for frontend
RUN cp frontend/nginx.conf /etc/nginx/conf.d/default.conf

# Create startup script to run all services
RUN echo '#!/bin/bash\n\
echo "Starting all services..."\n\
\n\
# Start nginx for frontend\n\
echo "Starting nginx for frontend..."\n\
nginx &\n\
NGINX_PID=$!\n\
\n\
# Start auth-service in background\n\
echo "Starting auth-service..."\n\
java -jar auth-service/target/auth-service-0.0.1-SNAPSHOT.jar &\n\
AUTH_PID=$!\n\
\n\
# Start notification-service in background\n\
echo "Starting notification-service..."\n\
java -jar notification-service/target/notification-service-0.0.1-SNAPSHOT.jar &\n\
NOTIFICATION_PID=$!\n\
\n\
echo "All services started. PIDs: Auth=$AUTH_PID, Notification=$NOTIFICATION_PID, Nginx=$NGINX_PID"\n\
\n\
# Wait for all processes\n\
wait $AUTH_PID $NOTIFICATION_PID $NGINX_PID' > /app/start.sh

RUN chmod +x /app/start.sh

# Expose ports for all services
EXPOSE 8080 8081 80

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080

# Start all services
CMD ["/app/start.sh"] 