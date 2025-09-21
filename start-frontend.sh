#!/bin/bash

# NSPACE Frontend Start Script
echo "🚀 Starting NSPACE Frontend..."

# Check if we're in the right directory
if [ ! -d "vue-frontend" ]; then
    echo "❌ Error: vue-frontend directory not found!"
    echo "Please run this script from the AIPROJEKT root directory."
    exit 1
fi

# Navigate to frontend directory
cd vue-frontend

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
fi

# Start development server
echo "🌟 Starting Vue.js development server..."
echo "📱 Frontend will be available at: http://localhost:5173"
echo "🛑 Press Ctrl+C to stop the server"
echo ""

npm run dev
