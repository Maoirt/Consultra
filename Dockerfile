FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the entire project
COPY . .

# Install Docker Compose and netcat for debugging
RUN apt-get update && apt-get install -y docker-compose netcat

# Make debug script executable
RUN chmod +x debug-env.sh

# Expose ports
EXPOSE 8080 8081 3000 5432

# Start the application with debug
CMD ["sh", "-c", "./debug-env.sh && docker-compose -f docker-compose.yml up -d"] 