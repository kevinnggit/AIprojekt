#!/bin/bash

# NSPACE Full Stack Start Script
echo "🚀 Starting NSPACE Full Stack Application..."

# Check if we're in the right directory
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ Error: docker-compose.yml not found!"
    echo "Please run this script from the AIPROJEKT root directory."
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running!"
    echo "Please start Docker Desktop and try again."
    exit 1
fi

echo "🐳 Starting all services with Docker Compose..."
echo "📱 Frontend: http://localhost:3000"
echo "☕ Java API: http://localhost:8080"
echo "🐍 Python API: http://localhost:8000"
echo "📚 FastAPI Docs: http://localhost:8000/docs"
echo "🛑 Press Ctrl+C to stop all services"
echo ""

# Start all services
docker compose up --build

echo ""
echo "🛑 All services stopped."
