#!/bin/bash

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Function to print colored messages
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# PIDs for cleanup
BACKEND_PID=""
FRONTEND_PID=""

# Cleanup function
cleanup() {
    print_info "Shutting down services..."
    
    if [ -n "$FRONTEND_PID" ] && kill -0 "$FRONTEND_PID" 2>/dev/null; then
        print_info "Stopping frontend (PID: $FRONTEND_PID)..."
        kill "$FRONTEND_PID" 2>/dev/null || true
        wait "$FRONTEND_PID" 2>/dev/null || true
    fi
    
    if [ -n "$BACKEND_PID" ] && kill -0 "$BACKEND_PID" 2>/dev/null; then
        print_info "Stopping backend (PID: $BACKEND_PID)..."
        kill "$BACKEND_PID" 2>/dev/null || true
        wait "$BACKEND_PID" 2>/dev/null || true
    fi
    
    print_success "Services stopped gracefully"
    exit 0
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

print_info "Starting RealWorld Example App..."
echo ""

# Check Node.js version
print_info "Checking Node.js version..."
if ! command -v node &> /dev/null; then
    print_error "Node.js is not installed. Please install Node.js version 14-16."
    exit 1
fi

NODE_VERSION=$(node -v | sed 's/v//' | cut -d '.' -f 1)
if [ "$NODE_VERSION" -lt 14 ] || [ "$NODE_VERSION" -gt 16 ]; then
    print_error "Node.js version $NODE_VERSION is not supported. Required: 14-16"
    print_info "If you have nvm installed, run: cd frontend && nvm use"
    exit 1
fi

print_success "Node.js version $NODE_VERSION detected"
echo ""

# Check and install frontend dependencies
if [ ! -d "frontend/node_modules" ]; then
    print_info "Frontend dependencies not found. Installing..."
    cd frontend
    npm install
    cd ..
    print_success "Frontend dependencies installed"
    echo ""
else
    print_info "Frontend dependencies already installed"
    echo ""
fi

# Start backend
print_info "Starting backend (Spring Boot) on port 8080..."
./gradlew bootRun > backend.log 2>&1 &
BACKEND_PID=$!
print_info "Backend started with PID: $BACKEND_PID"
echo ""

# Wait for backend to be ready
print_info "Waiting for backend to be ready..."
BACKEND_READY=false
MAX_ATTEMPTS=60
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s http://localhost:8080/tags > /dev/null 2>&1; then
        BACKEND_READY=true
        break
    fi
    
    if ! kill -0 "$BACKEND_PID" 2>/dev/null; then
        print_error "Backend process died unexpectedly"
        print_info "Check backend.log for details"
        exit 1
    fi
    
    ATTEMPT=$((ATTEMPT + 1))
    echo -ne "\r${BLUE}[INFO]${NC} Waiting for backend... ($ATTEMPT/$MAX_ATTEMPTS seconds)"
    sleep 1
done

echo ""

if [ "$BACKEND_READY" = false ]; then
    print_error "Backend failed to start within $MAX_ATTEMPTS seconds"
    print_info "Check backend.log for details"
    cleanup
    exit 1
fi

print_success "Backend is ready!"
echo ""

# Start frontend
print_info "Starting frontend (Next.js) on port 3000..."
cd frontend
npx next dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..
print_info "Frontend started with PID: $FRONTEND_PID"
echo ""

print_success "========================================"
print_success "Both services are running!"
print_success "========================================"
echo ""
print_info "Backend:  http://localhost:8080"
print_info "Frontend: http://localhost:3000"
echo ""
print_info "Press Ctrl+C to stop both services"
echo ""

# Wait for both processes
wait
