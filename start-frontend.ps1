# NSPACE Frontend Start Script (PowerShell)
Write-Host "🚀 Starting NSPACE Frontend..." -ForegroundColor Green

# Check if we're in the right directory
if (-not (Test-Path "vue-frontend")) {
    Write-Host "❌ Error: vue-frontend directory not found!" -ForegroundColor Red
    Write-Host "Please run this script from the AIPROJEKT root directory." -ForegroundColor Yellow
    exit 1
}

# Navigate to frontend directory
Set-Location vue-frontend

# Check if node_modules exists
if (-not (Test-Path "node_modules")) {
    Write-Host "📦 Installing dependencies..." -ForegroundColor Blue
    npm install
}

# Start development server
Write-Host "🌟 Starting Vue.js development server..." -ForegroundColor Green
Write-Host "📱 Frontend will be available at: http://localhost:5173" -ForegroundColor Cyan
Write-Host "🛑 Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

npm run dev
