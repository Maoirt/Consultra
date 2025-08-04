FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the entire project
COPY . .

# Install Docker Compose
RUN apt-get update && apt-get install -y docker-compose

# Expose ports
EXPOSE 8080 8081 3000 5432

# Start the application
CMD ["docker-compose", "up", "-d"] 