#!/bin/bash


set -e

echo "======================================"
echo "Starting RealWorld Application"
echo "======================================"
echo ""
echo "Backend will run on: http://localhost:8080"
echo "Frontend will run on: http://localhost:3000"
echo ""
echo "Press Ctrl+C to stop both applications"
echo ""

cleanup() {
    echo ""
    echo "======================================"
    echo "Shutting down applications..."
    echo "======================================"
    kill $(jobs -p) 2>/dev/null || true
    exit
}

trap cleanup SIGINT SIGTERM

echo "Starting backend (Spring Boot)..."
echo "--------------------------------------"
./gradlew bootRun 2>&1 | sed 's/^/[BACKEND] /' &
BACKEND_PID=$!

sleep 2

echo ""
echo "Starting frontend (Next.js)..."
echo "--------------------------------------"
cd frontend

if command -v nvm &> /dev/null; then
    echo "[FRONTEND] Using nvm to switch to Node 16..."
    export NVM_DIR="$HOME/.nvm"
    [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
    nvm use 16 2>&1 | sed 's/^/[FRONTEND] /'
fi

echo "[FRONTEND] Installing dependencies..."
npm install 2>&1 | sed 's/^/[FRONTEND] /'

echo "[FRONTEND] Starting dev server..."
npm run dev 2>&1 | sed 's/^/[FRONTEND] /' &
FRONTEND_PID=$!

cd ..

wait $BACKEND_PID $FRONTEND_PID
