# NSPACE Full Stack Start Script (PowerShell)
Write-Host "🚀 Starting NSPACE Full Stack Application..." -ForegroundColor Green

# Check if we're in the right directory
if (-not (Test-Path "docker-compose.yml")) {
    Write-Host "❌ Error: docker-compose.yml not found!" -ForegroundColor Red
    Write-Host "Please run this script from the AIPROJEKT root directory." -ForegroundColor Yellow
    exit 1
}

# Check if Docker is running
try {
    docker info | Out-Null
} catch {
    Write-Host "❌ Error: Docker is not running!" -ForegroundColor Red
    Write-Host "Please start Docker Desktop and try again." -ForegroundColor Yellow
    exit 1
}

Write-Host "🐳 Starting all services with Docker Compose..." -ForegroundColor Blue
Write-Host "📱 Frontend: http://localhost:3000" -ForegroundColor Cyan
Write-Host "☕ Java API: http://localhost:8080" -ForegroundColor Cyan
Write-Host "🐍 Python API: http://localhost:8000" -ForegroundColor Cyan
Write-Host "📚 FastAPI Docs: http://localhost:8000/docs" -ForegroundColor Cyan
Write-Host "🛑 Press Ctrl+C to stop all services" -ForegroundColor Yellow
Write-Host ""

# Start all services
docker compose up --build

Write-Host ""
Write-Host "🛑 All services stopped." -ForegroundColor Red
