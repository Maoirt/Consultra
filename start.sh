#!/bin/bash

echo "Starting Consultra application..."
echo "Current directory: $(pwd)"

# Check if we're in the right directory
if [ ! -f "docker-compose.yml" ]; then
    echo "Error: docker-compose.yml not found in current directory"
    echo "Please run this script from the Consultra directory"
    exit 1
fi

# Start the services
echo "Starting services with docker-compose..."
docker-compose up -d

echo "Services started successfully!"
echo "You can check the status with: docker-compose ps" 