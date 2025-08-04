FROM openjdk:21-jdk-slim

WORKDIR /app

# Install Maven and other dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    maven \
    curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Copy pom.xml files first for better caching
COPY auth-service/pom.xml ./auth-service/
COPY notification-service/pom.xml ./notification-service/

# Download Maven dependencies (this layer will be cached)
RUN cd auth-service && mvn dependency:go-offline -DskipTests
RUN cd notification-service && mvn dependency:go-offline -DskipTests

# Copy source code
COPY auth-service/ ./auth-service/
COPY notification-service/ ./notification-service/

# Build auth-service
RUN cd auth-service && mvn clean package -DskipTests

# Build notification-service
RUN cd notification-service && mvn clean package -DskipTests

# Create startup script to run Java services
RUN echo '#!/bin/bash\n\
echo "Starting Java services..."\n\
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
echo "All services started. PIDs: Auth=$AUTH_PID, Notification=$NOTIFICATION_PID"\n\
\n\
# Wait for all processes\n\
wait $AUTH_PID $NOTIFICATION_PID' > /app/start.sh

RUN chmod +x /app/start.sh

# Expose ports for Java services
EXPOSE 8080 8081

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080

# Start Java services
CMD ["/app/start.sh"] 